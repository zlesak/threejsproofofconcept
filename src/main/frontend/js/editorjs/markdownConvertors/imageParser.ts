import type { OutputBlockData } from '@editorjs/editorjs';
import { BlockParser, BlockFromMarkdownResult, genId } from './types';

const IMAGE_REGEX = /^!\[([^\]]*)]\(([^)]+)\)/;

export const imageParser: BlockParser = {
  type: 'image',
  toMarkdown(block: OutputBlockData): string | null {
    if (block.type !== 'image') return null;
    const data: any = (block as any).data || {};
    const url: string = data.file?.url || data.url || ''; //BASE64 - může trvat pro velké obrázky nebo větší množství, předělat?
    const caption: string = data.caption || '';
    if (!url) return null;
    return `![${caption.replace(/]/g, '')}](${url})`;
  },
  fromMarkdown(lines: string[], start: number): BlockFromMarkdownResult | null {
    const line = lines[start].trim();
    const m = IMAGE_REGEX.exec(line);
    if (!m) return null;
    const caption = m[1];
    const url = m[2];
    return {
      block: {
        id: genId(),
        type: 'image',
        data: { file: { url }, caption, withBorder: false, withBackground: false, stretched: false }
      },
      nextLine: start + 1
    };
  }
};

