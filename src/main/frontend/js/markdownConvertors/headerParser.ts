import type { OutputBlockData } from '@editorjs/editorjs';
import { BlockParser, BlockFromMarkdownResult, genId, inlineHtmlToMarkdown, inlineMarkdownToHtml } from './types';

export const headerParser: BlockParser = {
  type: 'header',
  toMarkdown(block: OutputBlockData): string | null {
    if (block.type !== 'header') return null;
    const data: any = (block as any).data || {};
    const level: number = data.level || 1;
    const text: string = data.text || '';
    const mdText = inlineHtmlToMarkdown(text).trim();
    const lvl = Math.min(6, Math.max(1, level));
    return `${'#'.repeat(lvl)} ${mdText}`;
  },
  fromMarkdown(lines: string[], start: number): BlockFromMarkdownResult | null {
    const line = lines[start];
    const m = /^(#{1,6})\s+(.*)$/.exec(line);
    if (!m) return null;
    const level = m[1].length;
    const mdText = m[2].trim();
    const html = inlineMarkdownToHtml(mdText);
    return { block: { id: genId(), type: 'header', data: { level, text: html } }, nextLine: start + 1 };
  }
};

