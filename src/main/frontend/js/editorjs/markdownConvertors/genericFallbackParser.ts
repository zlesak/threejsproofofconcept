import type { OutputBlockData } from '@editorjs/editorjs';
import { BlockParser, BlockFromMarkdownResult, genId } from './types';

// Fallback pro nepodporované bloky
// Formát: <!-- EJ-BLOCK:type {"data":{...}} -->
const COMMENT_REGEX = /^<!--\s*EJ-BLOCK:([a-zA-Z0-9_-]+)\s+(\{.*})\s*-->$/;

export const genericFallbackParser: BlockParser = {
  type: '*',
  toMarkdown(block: OutputBlockData): string | null {
    return `<!-- EJ-BLOCK:${block.type} ${JSON.stringify({ data: (block as any).data || {} })} -->`;
  },
  fromMarkdown(lines: string[], start: number): BlockFromMarkdownResult | null {
    const line = lines[start].trim();
    const m = COMMENT_REGEX.exec(line);
    if (!m) return null;
    try {
      const type = m[1];
      const json = JSON.parse(m[2]);
      const data = json.data ?? json;
      return { block: { id: genId(), type, data }, nextLine: start + 1 };
    } catch {
      return null;
    }
  }
};
