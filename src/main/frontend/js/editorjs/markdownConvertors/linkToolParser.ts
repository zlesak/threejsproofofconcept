import type { OutputBlockData } from '@editorjs/editorjs';
import { BlockParser, BlockFromMarkdownResult, genId, inlineHtmlToMarkdown, inlineMarkdownToHtml } from './types';

// Markdown link: [text](url)
const MD_LINK_REGEX = /^\[([^\]]+)]\((https?:[^)]+)\)$/;
// Plain URL line
const URL_REGEX = /^(https?:\/\/\S+)$/;

export const linkToolParser: BlockParser = {
  type: 'linkTool',
  toMarkdown(block: OutputBlockData): string | null {
    if (block.type !== 'linkTool') return null;
    const data: any = (block as any).data || {};
    const link: string = data.link || '';
    const title: string = data.meta?.title || data.meta?.description || '';
    if (!link) return null;
    if (title) return `[${inlineHtmlToMarkdown(title).trim()}](${link})`;
    return link;
  },
  fromMarkdown(lines: string[], start: number): BlockFromMarkdownResult | null {
    const line = lines[start].trim();
    if (!line) return null;
    let url: string | null = null;
    let title: string | null = null;
    let m = MD_LINK_REGEX.exec(line);
    if (m) {
      title = m[1].trim();
      url = m[2].trim();
    } else {
      m = URL_REGEX.exec(line);
      if (m) {
        url = m[1].trim();
      }
    }
    if (!url) return null;
    const titleHtml = title ? inlineMarkdownToHtml(title) : undefined;
    return {
      block: {
        id: genId(),
        type: 'linkTool',
        data: {
          link: url,
            meta: {
              title: titleHtml || url,
              description: '',
              image: { url: '' }
            }
        }
      },
      nextLine: start + 1
    };
  }
};
