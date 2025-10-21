import type { OutputBlockData } from '@editorjs/editorjs';

export interface BlockToMarkdown {
  toMarkdown(block: OutputBlockData): string | null;
}

export interface BlockFromMarkdownResult {
  block: OutputBlockData;
  nextLine: number;
}

export interface BlockFromMarkdown {
  fromMarkdown(lines: string[], start: number): BlockFromMarkdownResult | null;
}

export interface BlockParser extends BlockToMarkdown, BlockFromMarkdown {
  readonly type: string;
}

export function genId(): string {
  return 'b_' + Math.random().toString(36).slice(2, 10);
}

// Pomocné funkce pro inline formátování
// Custom pro TextureColorLinkTool: [[text|textureId|hexColor]]
export function inlineHtmlToMarkdown(html: string): string {
  if (!html) return '';
  let out = html;

  // <br> -> '  \n' (markdown hard line break pomocí dvou mezer)
  out = out.replace(/<br\s*\/?>(?:(?=\s)|)/gi, '  \n');

  out = out.replace(/<a\b([^>]*)>([\s\S]*?)<\/a>/gi, (match, attrs: string, inner: string) => {
    const texId = /data-texture-id="([^"]+)"/i.exec(attrs)?.[1];
    const hex = /data-hex-color="([^"]+)"/i.exec(attrs)?.[1];
    if (texId && hex) {
      const innerConverted = inlineHtmlToMarkdown(inner);
      const safeInner = innerConverted.replace(/\|/g, '\\|');
      return `[[${safeInner}|${texId}|${hex}]]`;
    }
    const href = /href="([^"]+)"/i.exec(attrs)?.[1];
    if (href) {
      const innerConverted = inlineHtmlToMarkdown(inner);
      return `[${innerConverted}](${href})`;
    }
    return inner;
  });

  // Bold -> **text**
  out = out.replace(/<(b|strong)\b[^>]*>([\s\S]*?)<\/\1>/gi, '**$2**');
  // Italic -> *text*
  out = out.replace(/<(i|em)\b[^>]*>([\s\S]*?)<\/\1>/gi, '*$2*');
  // Underline -> __text__
  out = out.replace(/<u\b[^>]*>([\s\S]*?)<\/u>/gi, '__$1__');
  // Strikethrough -> ~~text~~
  out = out.replace(/<(s|strike|del)\b[^>]*>([\s\S]*?)<\/\1>/gi, '~~$2~~');
  // underline / strikethrough podle class nebo style
  out = out.replace(/<span\b[^>]*class="[^"]*(underline)[^"]*"[^>]*>([\s\S]*?)<\/span>/gi, '__$2__');
  out = out.replace(/<span\b[^>]*class="[^"]*(strike|strikethrough)[^"]*"[^>]*>([\s\S]*?)<\/span>/gi, '~~$2~~');
  out = out.replace(/<span\b[^>]*style="[^"]*text-decoration:\s*underline[^"]*"[^>]*>([\s\S]*?)<\/span>/gi, '__$1__');
  out = out.replace(/<span\b[^>]*style="[^"]*text-decoration:\s*line-through[^"]*"[^>]*>([\s\S]*?)<\/span>/gi, '~~$1~~');

  out = out.replace(/<[^>]+>/g, '');
  return out;
}

export function inlineMarkdownToHtml(md: string): string {
  if (!md) return '';
  let out = md;

  function applyInlineFormatting(txt: string): string {
    if (!txt) return txt;
    // Bold (**)
    txt = txt.replace(/\*\*([\s\S]*?)\*\*/g, '<b>$1</b>');
    // Strikethrough (~~) -> <s class="cdx-strikethrough">...
    txt = txt.replace(/~~([\s\S]*?)~~/g, '<s class="cdx-strikethrough">$1</s>');
    // Underline (__) -> <u class="cdx-underline">...
    txt = txt.replace(/__([^_][\s\S]*?)__/g, '<u class="cdx-underline">$1</u>');
    // Italic (*)
    txt = txt.replace(/\*([^*][\s\S]*?)\*/g, '<i>$1</i>');
    return txt;
  }

  // Custom texture odkazy [[text|textureId|hexColor]]
  out = out.replace(/\[\[([^|\]]+?)\|([^|\]]+?)\|([^|\]]+?)]]/g, (_m, text, textureId, hexColor) => {
    const restoredText = text.replace(/\\\|/g, '|');
    const formatted = applyInlineFormatting(restoredText);
    return `<a href="#" data-texture-id="${textureId}" data-hex-color="${hexColor}">${formatted}</a>`;
  });

  // Běžné odkazy [text](url)
  out = out.replace(/\[([^\]]+)]\(([^)]+)\)/g, (_m, txt, href) => {
    const formatted = applyInlineFormatting(txt);
    return `<a href="${href}">${formatted}</a>`;
  });

  // Konce řádků
  out = out.replace(/<br\s*\/??\s*>/gi, '<br>');
  out = out.replace(/ {2,}\n/g, '<br>');
  out = out.replace(/\\\n/g, '<br>');

  out = applyInlineFormatting(out);

  out = out.replace(/\n+/g, ' ');

  return out.trim();
}
