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

@customElement('editor-js')
export class EditorJs extends LitElement {
  @state()
  private editor: EditorJS | undefined;

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
              }`;
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
      #editorjs .ce-toolbar__actions.ce-toolbar__actions--opened {
        position: absolute;
        right: 0px;
        z-index: 10;
      }
      #editorjs { position: relative;}
    `;
    container.appendChild(style);
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
  async getData(): Promise<OutputData> {
    await this.editorReadyPromise;
    if (!this.editor) {
      throw new Error('Editor not initialized in getData');
    }
    try {
      return await this.editor.save();
    } catch (error) {
      console.error('Error saving editor data in getData:', error);
      throw error;
    }
  }

  // @ts-ignore - Method is used by external components
  async getDataAsString(): Promise<string> {
    await this.editorReadyPromise;
    if (!this.editor) {
      throw new Error('Editor not initialized in getDataAsString');
    }
    try {
      const data = await this.editor.save();
      return JSON.stringify(data);
    } catch (error) {
      console.error('Error saving editor data as string in getDataAsString:', error);
      throw error;
    }
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
}

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
