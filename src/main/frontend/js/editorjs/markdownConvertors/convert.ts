import type { OutputData, OutputBlockData } from '@editorjs/editorjs';
import { paragraphParser } from './paragraphParser';
import { headerParser } from './headerParser';
import { listParser } from './listParser';
import { quoteParser } from './quoteParser';
import { tableParser } from './tableParser';
import { imageParser } from './imageParser';
import { delimiterParser } from './delimiterParser';
import { linkToolParser } from './linkToolParser';
import { genericFallbackParser } from './genericFallbackParser';
import type { BlockParser } from './types';

const parsers: BlockParser[] = [
  delimiterParser,
  headerParser,
  listParser,
  quoteParser,
  tableParser,
  imageParser,
  linkToolParser,
  paragraphParser,
  genericFallbackParser
];

export async function convertEditorJsToMarkdown(data: OutputData): Promise<string> {
  const out: string[] = [];
  for (const block of data.blocks || []) {
    const parser = parsers.find(p => p.type === block.type);
    const md = (parser ? parser.toMarkdown(block) : genericFallbackParser.toMarkdown(block)) || '';
    out.push(md.trim());
  }
  return out.filter(Boolean).join('\n\n') + '\n';
}

export async function convertMarkdownToEditorJs(md: string): Promise<OutputData> {
  const lines = md.replace(/\r\n?/g, '\n').split('\n');
  const blocks: OutputBlockData[] = [];
  let i = 0;
  while (i < lines.length) {
    if (!lines[i].trim()) { i++; continue; }
    let matched = false;
    for (const p of parsers) {
      const res = p.fromMarkdown(lines, i);
      if (res) {
        blocks.push(res.block);
        i = res.nextLine;
        matched = true;
        break;
      }
    }
    if (!matched) {
      const res = paragraphParser.fromMarkdown(lines, i);
      if (res) {
        blocks.push(res.block);
        i = res.nextLine;
      } else {
        i++;
      }
    }
  }
  return { time: Date.now(), version: '', blocks };
}
