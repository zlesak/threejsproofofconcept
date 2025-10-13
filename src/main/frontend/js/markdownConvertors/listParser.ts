import type { OutputBlockData } from '@editorjs/editorjs';
import { BlockParser, BlockFromMarkdownResult, genId, inlineHtmlToMarkdown, inlineMarkdownToHtml } from './types';

interface ListItem { content: string; items: ListItem[]; meta?: { checked?: boolean }; }
interface ListData { style: 'unordered' | 'ordered' | 'checklist'; items: ListItem[]; meta?: { start?: number; counterType?: string }; }

const RE_UNORDERED = /^(\s*)([-+*])\s+(.*)$/; // bullet
const RE_ORDERED = /^(\s*)(\d+)\.\s+(.*)$/; // číslo
const RE_CHECK = /^(\s*)([-+*])\s+\[([ xX])]\s+(.*)$/; // checklist pattern
const RE_LISTMETA = /^<!--\s*LISTMETA\s+(\{.*})\s*-->$/; // metadata pro list
const INDENT = 2;

function repeat(str: string, n: number): string { return new Array(n + 1).join(str); }

function isListLine(line: string): boolean {
  return RE_CHECK.test(line) || RE_ORDERED.test(line) || RE_UNORDERED.test(line);
}

function detectRootStyle(line: string): 'unordered' | 'ordered' | 'checklist' {
  if (RE_CHECK.test(line)) return 'checklist';
  if (RE_ORDERED.test(line)) return 'ordered';
  return 'unordered';
}

function parseTree(lines: string[], start: number): { style: 'unordered'|'ordered'|'checklist'; items: ListItem[]; next: number; meta?: { start?: number; counterType?: string } } {
  let i = start;
  const first = lines[i];
  const style = detectRootStyle(first);
  let meta: { start?: number; counterType?: string } | undefined;
  let baseIndent: number | null = null;
  const root: ListItem[] = [];
  const stack: { lvl: number; item: ListItem }[] = [];
  while (i < lines.length) {
    const raw = lines[i];
    if (!raw.trim()) break;
    if (!isListLine(raw)) break;
    let mC = RE_CHECK.exec(raw);
    let mO = RE_ORDERED.exec(raw);
    let mU = RE_UNORDERED.exec(raw);
    let indent: number; let content: string; let checked: boolean | undefined;
    if (mC && (style === 'checklist' || style === 'unordered')) { indent = mC[1].length; content = mC[4]; checked = /[xX]/.test(mC[3]); }
    else if (mO) { if (style !== 'ordered' && stack.length === 0) break; indent = mO[1].length; content = mO[3]; if (!meta && stack.length===0 && Number(mO[2])!==1) meta = { start: Number(mO[2]) }; }
    else if (mU) { if (style === 'ordered' && stack.length===0) break; indent = mU[1].length; content = mU[3]; }
    else break;
    if (baseIndent === null) baseIndent = indent;
    const rel = Math.max(0, Math.floor((indent - baseIndent) / INDENT));
    const item: ListItem = { content: inlineMarkdownToHtml(content.trim()), items: [], meta: {} };
    if (checked != null) item.meta!.checked = checked;
    if (rel === 0) { root.push(item); stack.length = 0; stack.push({ lvl: rel, item }); }
    else {
      while (stack.length && stack[stack.length-1].lvl >= rel) stack.pop();
      if (!stack.length) { root.push(item); stack.push({ lvl: rel, item }); }
      else { stack[stack.length-1].item.items.push(item); stack.push({ lvl: rel, item }); }
    }
    i++;
  }
  return { style, items: root, next: i, meta };
}

function serialize(items: ListItem[], style: 'unordered'|'ordered'|'checklist', level: number, start: number): string[] {
  const out: string[] = [];
  let counter = start;
  for (const it of items) {
    const indent = repeat(' ', level * INDENT);
    const text = inlineHtmlToMarkdown(it.content).trim();
    let line: string;
    if (style === 'ordered') line = `${indent}${counter}. ${text}`.trimEnd();
    else if (style === 'checklist') line = `${indent}- [${it.meta?.checked ? 'x' : ' '}] ${text}`.trimEnd();
    else line = `${indent}- ${text}`.trimEnd();
    out.push(line);
    if (it.items.length) out.push(...serialize(it.items, style, level+1, style==='ordered'?1:1));
    if (style==='ordered') counter++; // increment only at root chain per depth
  }
  return out;
}

export const listParser: BlockParser = {
  type: 'list',
  toMarkdown(block: OutputBlockData): string | null {
    if (block.type !== 'list') return null;
    const data: any = (block as any).data || {};
    let style: 'unordered'|'ordered'|'checklist' = (['ordered','checklist'].includes(data.style)? data.style: 'unordered');
    function norm(arr: any[]): ListItem[] { return Array.isArray(arr)? arr.map(r=>({ content: r.content ?? r.text ?? (typeof r==='string'? r: ''), items: norm(r.items||[]), meta: r.meta? { checked: r.meta.checked }: {} })) : []; }
    const items = norm(data.items || []);
    if (!items.length) return '';
    const lines: string[] = [];
    const start = (style==='ordered' && data.meta?.start && data.meta.start!==1)? Number(data.meta.start):1;
    const counterType = style==='ordered'? data.meta?.counterType: undefined;
    if (style==='ordered' && (start!==1 || counterType)) {
      const meta: any = {};
      if (start!==1) meta.start = start;
      if (counterType) meta.counterType = counterType;
      lines.push(`<!-- LISTMETA ${JSON.stringify(meta)} -->`);
    }
    lines.push(...serialize(items, style, 0, style==='ordered'? start: 1));
    return lines.join('\n');
  },
  fromMarkdown(lines: string[], startIdx: number): BlockFromMarkdownResult | null {
    let i = startIdx;
    let meta: any;
    const metaMatch = RE_LISTMETA.exec(lines[i]);
    if (metaMatch) { try { meta = JSON.parse(metaMatch[1]); i++; } catch { /* ignore */ } }
    if (i>=lines.length) return null;
    if (!isListLine(lines[i])) return null;
    const { style, items, next, meta: detected } = parseTree(lines, i);
    if (!items.length) return null;
    const data: ListData = { style, items };
    const mergedMeta = { ...(detected||{}), ...(meta||{}) };
    if (style==='ordered' && (mergedMeta.start||mergedMeta.counterType)) data.meta = mergedMeta;
    return { block: { id: genId(), type: 'list', data } as any, nextLine: next };
  }
};
