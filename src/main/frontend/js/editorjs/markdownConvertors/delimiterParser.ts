import type { OutputBlockData } from '@editorjs/editorjs';
import { BlockParser, BlockFromMarkdownResult, genId } from './types';

export const delimiterParser: BlockParser = {
  type: 'delimiter',
  toMarkdown(block: OutputBlockData): string | null {
    if (block.type !== 'delimiter') return null;
    return '---';
  },
  fromMarkdown(lines: string[], start: number): BlockFromMarkdownResult | null {
    const line = lines[start].trim();
    if (line === '---') {
      return { block: { id: genId(), type: 'delimiter', data: {} }, nextLine: start + 1 };
    }
    return null;
  }
};

