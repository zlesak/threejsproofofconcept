import type { OutputBlockData } from '@editorjs/editorjs';
import { BlockParser, BlockFromMarkdownResult, genId, inlineHtmlToMarkdown, inlineMarkdownToHtml } from './types';

function isTableLine(line: string): boolean {
  return /^\|.*\|$/.test(line.trim());
}

function isSeparatorLine(line: string): boolean {
  // | --- | --- |
  const trimmed = line.trim();
  if (!isTableLine(trimmed)) return false;
  const cells = trimmed.split('|').slice(1, -1).map(c => c.trim());
  return cells.every(c => /^:?-{3,}:?$/.test(c));
}

export const tableParser: BlockParser = {
  type: 'table',
  toMarkdown(block: OutputBlockData): string | null {
    if (block.type !== 'table') return null;
    const data: any = (block as any).data || {};
    const content: string[][] = data.content || [];
    const withHeadings: boolean = !!data.withHeadings;
    if (!content.length) return null;
    const rows: string[] = [];
    const renderRow = (row: string[]) => `| ${row.map(c => inlineHtmlToMarkdown(c).trim()).join(' | ')} |`;
    if (withHeadings) {
      const head = content[0];
      rows.push(renderRow(head));
      rows.push(`| ${head.map(() => '---').join(' | ')} |`);
      for (let r = 1; r < content.length; r++) rows.push(renderRow(content[r]));
    } else {
      for (const row of content) rows.push(renderRow(row));
    }
    return rows.join('\n');
  },
  fromMarkdown(lines: string[], start: number): BlockFromMarkdownResult | null {
    if (!isTableLine(lines[start])) return null;
    let i = start;
    const raw: string[] = [];
    while (i < lines.length && isTableLine(lines[i])) { raw.push(lines[i]); i++; }
    if (!raw.length) return null;
    let withHeadings = false;
    let rows: string[] = raw;
    if (raw.length >= 2 && isSeparatorLine(raw[1])) {
      withHeadings = true;
      rows = [raw[0], ...raw.slice(2)];
    }
    const content: string[][] = rows.map(r => r.trim().split('|').slice(1, -1).map(c => inlineMarkdownToHtml(c.trim())));
    return { block: { id: genId(), type: 'table', data: { withHeadings, content } }, nextLine: i };
  }
};
