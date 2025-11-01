import type { OutputBlockData } from '@editorjs/editorjs';
import { BlockParser, BlockFromMarkdownResult, genId, inlineHtmlToMarkdown, inlineMarkdownToHtml } from './types';

export const paragraphParser: BlockParser = {
  type: 'paragraph',
  toMarkdown(block: OutputBlockData): string | null {
    if (block.type !== 'paragraph') return null;
    const text: string = (block as any).data?.text || '';
    return inlineHtmlToMarkdown(text).trim();
  },
  fromMarkdown(lines: string[], start: number): BlockFromMarkdownResult | null {
    let i = start;
    if (i >= lines.length) return null;
    if (!lines[i].trim()) return null;
    // Paragraph se ukončí prázdnou řádkou nebo strukturálním tokenem
    const collected: string[] = [];
    while (i < lines.length && lines[i].trim() && !isStructuralLine(lines[i])) {
      collected.push(lines[i]);
      i++;
    }
    if (!collected.length) return null;
    const md = collected.join('\n');
    const html = inlineMarkdownToHtml(md);
    return { block: { id: genId(), type: 'paragraph', data: { text: html } }, nextLine: i };
  }
};

function isStructuralLine(line: string): boolean {
  const trimmed = line.trim();
  return /^(#{1,6})\s+/.test(trimmed) || // heading
    /^(-|\*|\+|\d+\.)\s+/.test(trimmed) || // list item
    /^>\s+/.test(trimmed) || // quote
    /^\|.*\|$/.test(trimmed) || // table row
    /^---$/.test(trimmed) || // delimiter
    /^!\[[^\]]*]\([^)]*\)/.test(trimmed) || // image
    /^- \[[ xX]] /.test(trimmed) || // checklist item
    /^<!--\s*EJ-BLOCK:/.test(trimmed); // fallback
}
