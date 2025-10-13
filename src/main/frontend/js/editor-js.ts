import { LitElement } from 'lit';
import { customElement, state } from 'lit/decorators.js';
import EditorJS, { OutputData } from '@editorjs/editorjs';
import Header from '@editorjs/header';
import List from '@editorjs/list';
import Paragraph from '@editorjs/paragraph';
import Underline from '@editorjs/underline';
import Quote from '@editorjs/quote';
import Table from '@editorjs/table';
import Hyperlink from 'editorjs-hyperlink';
import Strikethrough from '@sotaproject/strikethrough';
import uploader from '@ajite/editorjs-image-base64';
import ImageTool from '@editorjs/image';
import LinkTool from '@editorjs/link';
import TextureColorLinkTool from './textureColorLinkTool/textureColorLinkTool';
import '@vaadin/markdown';
import { convertEditorJsToMarkdown, convertMarkdownToEditorJs } from './markdownConvertors/convert';

@customElement('editor-js')
export class EditorJs extends LitElement {
  @state()
  private editor: EditorJS | undefined;

  @state()
  private markdownMode: boolean = false;
  @state()
  private markdownText: string = '';

  readonly editorReadyPromise: Promise<void>;
  private resolveEditorReadyPromise!: () => void;
  private _chapterContentData: OutputData = { time: Date.now(), blocks: [], version: '' };

  constructor() {
    super();
    this.editorReadyPromise = new Promise(resolve => {
      this.resolveEditorReadyPromise = resolve;
    });
  }

  createRenderRoot() {
    return this;
  }

  connectedCallback() {
    super.connectedCallback();
    this.style.display = 'block';
    this.style.width = '100%';

    const style = document.createElement('style');
    style.textContent = `
              .ce-block__content, .ce-toolbar__content {
                  width: 100% !important;
                  max-width: 100% !important;
              }
              .codex-editor__redactor {
                  padding-bottom: 0 !important;
              }              
              .editor-search-highlight {
                background-color: #ffeb3b;
                color: #000000;
                font-weight: bold;
              }
              #editorjs-md { font-family: monospace; width: 100%; min-height: 300px; resize: vertical; box-sizing: border-box; padding: 8px; line-height: 1.4; }
              .markdown-mode-banner { font-size: 12px; color: #555; margin: 4px 0 8px; font-style: italic;}
              `;
    this.appendChild(style);
  }

  async firstUpdated() {
    await this.initializeEditor();
    attachTextureColorListeners();
  }

  async initializeContainer() {
    let container = document.createElement('div');
    container.id = 'editorjs';
    container.style.width = '100%';
    container.style.minHeight = '300px';

    this.appendChild(container);
    const style = document.createElement('style');
    style.innerHTML = `
      #editorjs .ce-toolbar__actions.ce-toolbar__actions--opened { position: absolute; right: 0px; z-index: 10; }
      #editorjs { position: relative; }
      #markdown-wrapper { display: none; width: 100%; min-height: 300px; box-sizing: border-box; gap: 8px; }
      #markdown-wrapper.show { display: flex; }
      #markdown-wrapper textarea { flex: 1; font-family: monospace; resize: vertical; padding: 8px; line-height: 1.4; }
      #markdown-preview { flex: 1; overflow: auto; border-left: 1px solid #ddd; padding-left: 8px; }
      #markdown-toolbar { display: none; margin-bottom: 4px; gap: 8px; }
      #markdown-toolbar.show { display: flex; }
      #markdown-toolbar button { font-size: 12px; padding: 4px 8px; cursor: pointer; }
    `;
    container.appendChild(style);

    const toolbar = document.createElement('div');
    toolbar.id = 'markdown-toolbar';
    const btnTogglePreview = document.createElement('button');
    btnTogglePreview.textContent = 'Skryt náhled';
    btnTogglePreview.addEventListener('click', () => {
      const preview = this.querySelector('#markdown-preview') as HTMLElement;
      if (preview.style.display === 'none') {
        preview.style.display = 'block';
        btnTogglePreview.textContent = 'Skryt náhled';
      } else {
        preview.style.display = 'none';
        btnTogglePreview.textContent = 'Zobraz náhled';
      }
    });
    toolbar.appendChild(btnTogglePreview);
    this.insertBefore(toolbar, container);

    const mdWrapper = document.createElement('div');
    mdWrapper.id = 'markdown-wrapper';

    const mdArea = document.createElement('textarea');
    mdArea.id = 'editorjs-md';
    mdArea.placeholder = 'Markdown obsah...';

    const preview = document.createElement('div');
    preview.id = 'markdown-preview';
    const mdRender = document.createElement('vaadin-markdown');
    mdRender.setAttribute('id', 'md-render');
    preview.appendChild(mdRender);

    mdArea.addEventListener('input', () => {
      this.markdownText = mdArea.value;
      (mdRender as any).markdown = this.markdownText;
    });

    mdWrapper.appendChild(mdArea);
    mdWrapper.appendChild(preview);
    this.appendChild(mdWrapper);

    const banner = document.createElement('div');
    banner.className = 'markdown-mode-banner';
    banner.id = 'editorjs-md-banner';
    banner.style.display = 'none';
    banner.textContent = 'Markdown režim. Uložení převede MD do bloků pro Editor JS editor. Některé MD anotace, které nemají ekvivalent v EdotrJS, nemusí přežít!';
    this.insertBefore(banner, mdWrapper);

    return container;
  }

  private async initializeEditor() {
    let container = await this.initializeContainer();
    try {
      this.editor = new EditorJS({
        holder: container,
        placeholder: 'Začněte tvořit...',
        tools: {
          strikethrough: Strikethrough,
          linkTool: LinkTool,
          underline: Underline,
          paragraph: {
            class: Paragraph,
            inlineToolbar: ['bold', 'strikethrough', 'underline', 'italic', 'textureColorLinkTool']
          },
          header: {
            class: Header,
            inlineToolbar: ['underline', 'bold', 'italic'],
            config: {
              placeholder: 'Vložte nadpis',
              levels: [1, 2, 3, 4, 5, 6],
              defaultLevel: 1
            }
          },
          table: Table,
          list: List,
          quote: {
            class: Quote,
            inlineToolbar: ['underline', 'bold', 'italic'],
            config: {
              quotePlaceholder: 'Citujte...',
              captionPlaceholder: 'Podepište autora citace'
            }
          },
          //TODO: Implement BE API endpoint in order to be able to use attaches tool
          // attaches: {
          //     class: AttachesTool,
          //     config: {
          //         endpoint: 'http://localhost:8008/uploadFile'
          //     }
          // },
          hyperlink: {
            class: Hyperlink,
            config: {
              target: '_self',
              rel: 'nofollow',
              availableTargets: ['_self'],
              availableRels: ['author', 'noreferrer'],
              validate: false,
              inlineToolbar: ['underline', 'bold', 'italic']
            }
          },
          textureColorLinkTool: {
            class: TextureColorLinkTool,
            config: {
              textures: [],
              colors: []
            }
          },
          image: {
            class: ImageTool,
            config: {
              uploader
            }
          }
        },
        i18n: {
          messages: {
            toolNames: {
              'Text': 'Text', 'Heading': 'Nadpis', 'List': 'Seznam',
              'Warning': 'Varování', 'Quote': 'Citace', 'Code': 'Kód',
              'Table': 'Tabulka', 'Link': 'Odkaz (mimo MISH)', 'Marker': 'Zvýrazňovač',
              'Bold': 'Tučně', 'Italic': 'Italika', 'InlineCode': 'Řádkový kód',
              'Ordered List': 'Seřazený Seznam', 'Unordered List': 'Neseřazený Seznam',
              'Checklist': 'Odškrtávací seznam', 'Image': 'Obrázek', 'Attaches': 'Přílohy',
              'Hyperlink': 'Hypertextový odkaz', 'Strikethrough': 'Přeškrtnutí'
            },
            tools: {
              'header': {
                'Heading 1': 'Nadpis úrovně 1',
                'Heading 2': 'Nadpis úrovně 2',
                'Heading 3': 'Nadpis úrovně 3',
                'Heading 4': 'Nadpis úrovně 4',
                'Heading 5': 'Nadpis úrovně 5',
                'Heading 6': 'Nadpis úrovně 6'
              },
              'list': {
                'Unordered': 'Neseřazený seznam',
                'Ordered': 'Seřazený seznam',
                'Checklist': 'Odškrtávací seznam'
              },
              'image': {
                'With border': 'Přidat ohraničení',
                'Stretch image': 'Roztáhnout na celou šířku',
                'With background': 'Přidat pozadí za obrázek'
              },
              'table': {
                'With headings': 'Se záhlavím',
                'Without headings': 'Bez záhlaví',
                'Stretch': 'Roztáhnout',
                'Collapse': 'Stáhnout',
                'Add column to left': 'Přidat sloupec vlevo',
                'Add column to right': 'Přidat sloupec vpravo',
                'Delete column': 'Smazat sloupec',
                'Add row above': 'Přidat řádek nahoru',
                'Add row below': 'Přidat řádek dolu',
                'Delete row': 'Smazat řádek'
              },
              'tools': {
                'hyperlink': {
                  'Save': 'Uložit',
                  'Select target': 'Zvolte odkaz do textury',
                  'Select rel': 'Zvolte rel'
                }
              }
            },
            blockTunes: {
              'delete': {
                'Delete': 'Smazat blok',
                'Click to delete': 'Potvrďte smazání'
              },
              'moveUp': {
                'Move up': 'Posunout nahoru'
              },
              'moveDown': {
                'Move down': 'Posunout dolu'
              }
            },
            ui: {
              'popover': {
                'Filter': 'Filtrovat',
                'Nothing found': 'Nic nenalezeno',
                'Convert to': 'Převést na...'
              },
              'toolbar': {
                'toolbox': {
                  'Add': 'Přidat další blok'
                }
              },
              'blockTunes': {
                'toggler': {
                  'Click to tune': 'Vyberte nastavení bloku',
                  'or drag to move': ''
                }
              }
            }
          }
        },
        onReady: () => {
          this.resolveEditorReadyPromise();
          this.dispatchEvent(new CustomEvent('editor-js-ready', { bubbles: true, composed: true }));
          attachTextureColorListeners();
        },
        onChange: () => {
          attachTextureColorListeners();
        }
      });
    } catch (error) {
      console.error('initializeEditor: Error creating EditorJS instance:', error);
      throw error;
    }
  }

  // @ts-ignore - Method is used by external components
  public async toggleMarkdownMode(): Promise<void> {
    await this.editorReadyPromise;
    const mdWrapper = this.querySelector('#markdown-wrapper') as HTMLDivElement | null;
    const mdArea = this.querySelector('#editorjs-md') as HTMLTextAreaElement | null;
    const mdRender = this.querySelector('#md-render') as any;
    const banner = this.querySelector('#editorjs-md-banner') as HTMLDivElement | null;
    const toolbar = this.querySelector('#markdown-toolbar') as HTMLDivElement | null;
    const editorDiv = this.querySelector('#editorjs') as HTMLElement | null;
    if (!mdWrapper || !mdArea || !editorDiv) return;

    if (!this.markdownMode) {
      if (!this.editor) return;
      try {
        const data = await this.editor.save();
        this.markdownText = await convertEditorJsToMarkdown(data);
        mdArea.value = this.markdownText;
        if (mdRender) mdRender.markdown = this.markdownText;
        editorDiv.style.display = 'none';
        mdWrapper.classList.add('show');
        if (banner) banner.style.display = 'block';
        if (toolbar) toolbar.classList.add('show');
        if (this.editor && !this.editor.readOnly.isEnabled) await this.editor.readOnly.toggle(true);
        this.markdownMode = true;
        this.dispatchEvent(new CustomEvent('markdown-mode-changed', { detail: { markdownMode: true }, bubbles: true, composed: true }));
      } catch (e) {
        console.error('Chyba při převodu do Markdownu:', e);
      }
    } else {
      try {
        const outputData = await convertMarkdownToEditorJs(this.markdownText);
        await this.setData(outputData);
      } catch (e) {
        console.error('Chyba při převodu z Markdownu do EditorJS:', e);
      } finally {
        mdWrapper.classList.remove('show');
        editorDiv.style.display = 'block';
        if (banner) banner.style.display = 'none';
        if (toolbar) toolbar.classList.remove('show');
        if (this.editor && this.editor.readOnly.isEnabled) await this.editor.readOnly.toggle(false);
        this.markdownMode = false;
        this.dispatchEvent(new CustomEvent('markdown-mode-changed', { detail: { markdownMode: false }, bubbles: true, composed: true }));
      }
    }
  }

  // @ts-ignore - Method is used by external components
  public async getMarkdown(): Promise<string> {
    await this.editorReadyPromise;
    if (this.markdownMode) {
      return this.markdownText;
    }
    if (!this.editor) return '';
    try {
      const data = await this.editor.save();
      return await convertEditorJsToMarkdown(data);
    } catch (e) {
      console.error('getMarkdown error:', e);
      return '';
    }
  }

  // @ts-ignore - Method is used by external components
  public async loadMarkdown(md: string): Promise<void> {
    await this.editorReadyPromise;
    try {
      const outputData = await convertMarkdownToEditorJs(md);
      await this.setData(outputData);
      if (this.markdownMode) {
        const mdArea = this.querySelector('#editorjs-md') as HTMLTextAreaElement | null;
        if (mdArea) mdArea.value = md;
        this.markdownText = md;
      }
    } catch (e) {
      console.error('loadMarkdown error:', e);
    }
  }

  // @ts-ignore - Method is used by external components
  public isMarkdownMode(): boolean {
    return this.markdownMode;
  }

  // @ts-ignore - Method is used by external components
  async getData(): Promise<OutputData> {
    await this.editorReadyPromise;
    if (this.markdownMode) {
      try {
        return await convertMarkdownToEditorJs(this.markdownText);
      } catch (e) {
        console.error('Konverze markdown -> bloky selhala v getData:', e);
        return { time: Date.now(), version: '', blocks: [] };
      }
    }
    if (!this.editor) throw new Error('Editor not initialized in getData');
    try { return await this.editor.save(); } catch (error) { console.error('Error saving editor data in getData:', error); throw error; }
  }

  // @ts-ignore - Method is used by external components
  async getDataAsString(): Promise<string> {
    const data = await this.getData();
    return JSON.stringify(data);
  }

  // @ts-ignore - Method is used by external components
  async setChapterContentData(jsonData: string): Promise<void> {
    await this.editorReadyPromise;
    if (!this.editor || !this.editor.blocks) {
      console.error('setData: Editor or editor.blocks not fully initialized even after promise resolved.');
      return;
    }
    this._chapterContentData = JSON.parse(jsonData);
    await this.setData(this._chapterContentData);
    attachTextureColorListeners();
  }

  // @ts-ignore - Method is used by external components
  async setSelectedSubchapterData(jsonData: string): Promise<void> {
    await this.editorReadyPromise;
    if (!this.editor || !this.editor.blocks) {
      console.error('setData: Editor or editor.blocks not fully initialized even after promise resolved.');
      return;
    }
    const finalSubChapterDataOutputData: OutputData = structuredClone(this._chapterContentData);
    finalSubChapterDataOutputData.blocks = JSON.parse(jsonData);
    await this.setData(finalSubChapterDataOutputData);
    attachTextureColorListeners();
  }

  // @ts-ignore - Method is used by external components
  async showWholeChapterData() {
    await this.editorReadyPromise;
    if (!this.editor || !this.editor.blocks) {
      console.error('setData: Editor or editor.blocks not fully initialized even after promise resolved.');
      return;
    }
    await this.setData(this._chapterContentData);
  }

  async setData(value: OutputData): Promise<void> {
    await this.editorReadyPromise;
    if (!this.editor || !this.editor.blocks) {
      console.error('setData: Editor or editor.blocks not fully initialized even after promise resolved.');
      return;
    }
    try {
      await this.editor.blocks.clear();
      await this.editor.blocks.render(value);
      attachTextureColorListeners();
    } catch (error) {
      console.error('Error setting editor data:', error);
      throw error;
    }
  }

  // @ts-ignore - Method is used by external components
  public async toggleReadOnlyMode(readOnly?: boolean) {
    await this.editorReadyPromise;
    if (!this.editor) {
      throw new Error('Editor not initialized in toggleReadOnlyMode');
    }
    if (readOnly == null) {
      readOnly = !this.editor.readOnly.isEnabled;
    }
    await this.editor.readOnly.toggle(readOnly);
  }

  public async clear() {
    await this.editorReadyPromise;
    if (!this.editor) {
      throw new Error('Editor not initialized in clear');
    }
    this.editor.clear();
  }

  // @ts-ignore - Method is used by external components
  public async initializeTextureSelects(texturesJson?: string, areasJson?: string): Promise<void> {
    await this.editorReadyPromise;
    if (!this.editor || !this.editor.blocks) {
      console.error('initializeTextureSelects: Editor or editor.blocks not fully initialized even after promise resolved.');
      return;
    }
    try {
      let textures = [];
      let colors = [];
      if (texturesJson) {
        textures = JSON.parse(texturesJson);
      }
      if (areasJson) {
        colors = JSON.parse(areasJson);
      }
      TextureColorLinkTool.setGlobalTexturesAndColors(textures, colors);
    } catch (error) {
      console.error('Error initializing texture selects:', error);
      throw error;
    }
  }

  // @ts-ignore - Method is used by external components
  public async search(searchText: string): Promise<void> {
    await this.editorReadyPromise;
    if (this.markdownMode) { return; }
    if (!this.editor || !this.editor.blocks) { console.error('search: editor not ready'); return; }
    this.clearSearchHighlights();
    if (!searchText || searchText.trim() === '') return;
    const searchTextLower = searchText.toLowerCase();

    for (let i = 0; i < this.editor.blocks.getBlocksCount(); i++) {
      const blockElement = this.editor.blocks.getBlockByIndex(i)!.holder;
      const walker = document.createTreeWalker(blockElement, NodeFilter.SHOW_TEXT, null);
      let node: Node | null;
      while (node = walker.nextNode()) {
        if (node.nodeType !== Node.TEXT_NODE) break;

        const textContent = node.textContent || '';
        const textContentLower = textContent.toLowerCase();

        if (!textContentLower.includes(searchTextLower)) break;

        const parent = node.parentNode;
        if (!parent) break;

        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = textContent.replace(new RegExp(`(${searchText})`, 'gi'), `<span class="editor-search-highlight" match="true">$1</span>`);

        const fragment = document.createDocumentFragment();
        while (tempDiv.firstChild) {
          fragment.appendChild(tempDiv.firstChild);
        }
        parent.replaceChild(fragment, node);
      }
    }
  }

  private clearSearchHighlights(): void {
    const highlightedElements = document.getElementById('editorjs')?.querySelectorAll('[match="true"]');
    if (highlightedElements === undefined) return;
    highlightedElements.forEach(element => {
      const parent = element.parentNode;
      if (parent) {
        const textNode = document.createTextNode(element.textContent || '');
        parent.replaceChild(textNode, element);
        parent.normalize();
      }
    });
  }
}

// @ts-ignore
function attachTextureColorListeners() {
  const editorContainer = document.getElementById('editorjs');
  if (!editorContainer) return;
  const links = editorContainer.querySelectorAll('a[data-texture-id][data-hex-color]');
  links.forEach(link => {
    if (!link.hasAttribute('data-texture-color-listener')) {
      link.addEventListener('click', (event) => {
        event.preventDefault();
        let customElement = link.closest('editor-js');
        if (customElement) {
          customElement.dispatchEvent(new CustomEvent('texturecolorareaclick', {
            bubbles: true,
            composed: true,
            detail: {
              textureId: link.getAttribute('data-texture-id'),
              hexColor: link.getAttribute('data-hex-color'),
              text: link.textContent
            }
          }));
        }
      });
      link.setAttribute('data-texture-color-listener', 'true');
    }
  });
}
