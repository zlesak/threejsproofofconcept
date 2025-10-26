import { LitElement } from 'lit';
import { customElement, state } from 'lit/decorators.js';
import { initializeEditor } from './editorjs-init';
import { attachTextureColorListeners } from './texture-utils';
import { searchInEditor } from './search-utils';
import { convertEditorJsToMarkdown, convertMarkdownToEditorJs } from 'Frontend/js/editorjs/markdownConvertors/convert';
import TextureColorLinkTool from 'Frontend/js/editorjs/textureColorLinkTool/textureColorLinkTool';
import { OutputData } from '@editorjs/editorjs';

@customElement('editor-js')
export class EditorJs extends LitElement {
  @state()
  private editor: any;

  readonly editorReadyPromise: Promise<void>;
  private resolveEditorReadyPromise!: () => void;
  private rejectEditorReadyPromise!: (reason?: any) => void;
  private _chapterContentData: OutputData = { time: Date.now(), blocks: [], version: '' };

  constructor() {
    super();
    this.editorReadyPromise = new Promise((resolve, reject) => {
      this.resolveEditorReadyPromise = resolve;
      this.rejectEditorReadyPromise = reject;
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
              `;
    this.appendChild(style);
  }

  async firstUpdated() {
    try {
      this.editor = await initializeEditor(this);
      TextureColorLinkTool.setGlobalModelsTexturesAndColors([], [], []);
      this.resolveEditorReadyPromise();
      attachTextureColorListeners();
    } catch (e) {
      console.error('Editor initialization failed in firstUpdated:', e);
      this.rejectEditorReadyPromise(e);
      throw e;
    }
  }

// @ts-ignore - Method is used by external components
  public async search(searchText: string): Promise<void> {
    await searchInEditor(this.editor, searchText, this.editorReadyPromise);
  }

  // @ts-ignore - Method is used by external components
  public async getMarkdown(): Promise<string> {
    await this.editorReadyPromise;
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
    } catch (e) {
      console.error('loadMarkdown error:', e);
    }
  }

  // @ts-ignore - Method is used by external components
  async getData(): Promise<any> {
    await this.editorReadyPromise;
    if (!this.editor) throw new Error('Editor not initialized in getData');
    try {
      return JSON.stringify(await this.editor.save());
    } catch (error) {
      console.error('Error saving editor data in getData:', error);
      throw error;
    }
  }

  // @ts-ignore - Method is used by external components
  async setChapterContentData(jsonData: string): Promise<void> {
    await this.editorReadyPromise;
    if (!this.editor || !this.editor.blocks) {
      console.error('setChapterContentData: Editor or editor.blocks not fully initialized even after promise resolved.');
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
      console.error('setSelectedSubchapterData: Editor or editor.blocks not fully initialized even after promise resolved.');
      return;
    }
    const finalSubChapterDataOutputData: any = structuredClone(this._chapterContentData);
    finalSubChapterDataOutputData.blocks = JSON.parse(jsonData);
    await this.setData(finalSubChapterDataOutputData);
    attachTextureColorListeners();
  }

  // @ts-ignore - Method is used by external components
  async showWholeChapterData() {
    await this.editorReadyPromise;
    if (!this.editor || !this.editor.blocks) {
      console.error('showWholeChapterData: Editor or editor.blocks not fully initialized even after promise resolved.');
      return;
    }
    await this.setData(this._chapterContentData);
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

  // @ts-ignore - Method is used by external components
  public async initializeModelTextureAreaSelects(modelsJson?: string, texturesJson?: string, areasJson?: string): Promise<void> {
    await this.editorReadyPromise;
    if (!this.editor || !this.editor.blocks) {
      console.error('initializeModelTextureAreaSelects: Editor or editor.blocks not fully initialized even after promise resolved.');
      return;
    }
    try {
      let textures = [];
      let colors = [];
      let models = [];
      if (texturesJson) {
        textures = JSON.parse(texturesJson);
      }
      if (areasJson) {
        colors = JSON.parse(areasJson);
      }
      if (modelsJson) {
        models = JSON.parse(modelsJson);
      }
      TextureColorLinkTool.setGlobalModelsTexturesAndColors(models, textures, colors);
    } catch (error) {
      console.error('Error initializing texture selects:', error);
      throw error;
    }
  }

  // @ts-ignore - Method is used by external components
  public async getSubchaptersNames(): Promise<string> {
    const subChapters: Record<string, string> = {};
    if (!this.editor || !this.editor.blocks) {
      console.error('getSubchaptersNamesFromBlocks: Editor or editor.blocks not fully initialized even after promise resolved.');
      return JSON.stringify(subChapters);
    }
    try {
      const data = await this.editor.save() ?? { blocks: [] as any[] };
      const blocks = data.blocks ?? [];
      for (let i = 0; i < blocks.length; i++) {
        const block = blocks[i];
        if (block.type === 'header' && block.data && block.data.level === 1) {

          let id: string;
          if (block.id) {
            id = String(block.id);
          } else {
            id = `fallback-${(typeof crypto !== 'undefined' && (crypto as any).randomUUID ? (crypto as any).randomUUID() : Math.random().toString(36).slice(2)).slice(0, 7)}`;
            block.id = id;
          }
          subChapters[id] = String(block.data.text ?? '');
        }
      }
      return JSON.stringify(subChapters);
    } catch (error) {
      console.error('Error in getSubchaptersNames: ', error);
      throw error;
    }
  }
}
