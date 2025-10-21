import type { OutputBlockData } from '@editorjs/editorjs';
import { BlockParser, BlockFromMarkdownResult, genId, inlineHtmlToMarkdown, inlineMarkdownToHtml } from './types';

export const quoteParser: BlockParser = {
  type: 'quote',
  toMarkdown(block: OutputBlockData): string | null {
    if (block.type !== 'quote') return null;
    const data: any = (block as any).data || {};
    const text: string = data.text || '';
    const caption: string = data.caption || '';
    const mdLines = inlineHtmlToMarkdown(text).split(/\n+/).map(l => `> ${l}`);
    if (caption) mdLines.push(`> — ${inlineHtmlToMarkdown(caption).trim()}`);
    return mdLines.join('\n');
  },
  fromMarkdown(lines: string[], start: number): BlockFromMarkdownResult | null {
    if (!/^>\s+/.test(lines[start])) return null;
    let i = start;
    const collected: string[] = [];
    let caption = '';
    while (i < lines.length && /^>\s+/.test(lines[i])) {
      const content = lines[i].replace(/^>\s+/, '');
      if (/^—\s+/.test(content)) {
        caption = content.replace(/^—\s+/, '').trim();
      } else {
        collected.push(content);
      }
      i++;
    }
    if (!collected.length && !caption) return null;
    const mdText = collected.join('\n');
    const html = inlineMarkdownToHtml(mdText);
    const captionHtml = inlineMarkdownToHtml(caption);
    return { block: { id: genId(), type: 'quote', data: { text: html, caption: captionHtml, alignment: 'left' } }, nextLine: i };
  }
};

