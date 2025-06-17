import {LitElement} from 'lit';
import {customElement, state} from 'lit/decorators.js';
import EditorJS, {OutputData} from '@editorjs/editorjs';
import Header from '@editorjs/header';
import List from '@editorjs/list';
import LinkTool from '@editorjs/link';
import Checklist from '@editorjs/checklist';
import Paragraph from '@editorjs/paragraph';
import AttachesTool from '@editorjs/attaches';
import Underline from '@editorjs/underline';
import Quote from '@editorjs/quote';
import Table from '@editorjs/table';
import Hyperlink from 'editorjs-hyperlink';
import Strikethrough from '@sotaproject/strikethrough';
import uploader from '@ajite/editorjs-image-base64';
import ImageTool from '@editorjs/image';

@customElement('editor-js')
export class EditorJs extends LitElement {
    @state()
    private editor: EditorJS | undefined;

    private editorReadyPromise: Promise<void>;
    private resolveEditorReadyPromise!: () => void;

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
        `;
        this.appendChild(style);
    }

    async firstUpdated() {
        await this.initializeEditor();
///this style is hero to move the toolbar to the left to overcome the issue with it not being displayed correctly in the editor
        const style = document.createElement('style');
        style.textContent = `
.ce-block {
  position: relative;
}

.ce-toolbar {
  left: auto !important;
  right: 0 !important;
  flex-direction: row-reverse !important;
}

.ce-toolbar__plus {
  margin-left: 8px;
  margin-right: 0;
}
`;
        this.appendChild(style);
    }

    async initializeContainer() {
        let container = document.createElement('div');
        container.id = "editorjs";
        container.style.width = '100%';
        container.style.height = '100%';
        container.style.overflowY = 'auto';
        this.appendChild(container);
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
                    underline: Underline,
                    checklist: Checklist,
                    paragraph: Paragraph,
                    linkTool: LinkTool,
                    header: Header,
                    table: Table,
                    list: List,
                    quote: {
                        class: Quote,
                        config: {
                            quotePlaceholder: 'Vložte citaci...',
                            captionPlaceholder: 'Autor citace',
                        },
                    },
                    attaches: {
                        class: AttachesTool,
                        config: {
                            endpoint: 'http://localhost:8008/uploadFile'
                        }
                    },
                    hyperlink: {
                        class: Hyperlink,
                        config: {
                            shortcut: 'CMD+L',
                            target: '_self',
                            rel: 'nofollow',
                            availableTargets: ['_self'],
                            availableRels: ['author', 'noreferrer'],
                            validate: false,
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
                            "Text": "Text", "Heading": "Nadpis", "List": "Seznam",
                            "Warning": "Varování", "Quote": "Citace", "Code": "Kód",
                            "Table": "Tabulka", "Link": "Odkaz", "Marker": "Zvýrazňovač",
                            "Bold": "Tučně", "Italic": "Italika", "InlineCode": "Řádkový kód",
                            "Ordered List": "Seřazený Seznam", "Unordered List": "Neseřazený Seznam",
                            "Checklist": "Odškrtávací seznam"
                        },
                        tools: {
                            "List": {
                                'Unordered': 'Neseřazený',
                                'Ordered': 'Seřazený',
                                'Checklist': 'Odškrtávací',
                            }
                        },
                    },
                },
                onReady: () => {
                    this.resolveEditorReadyPromise();
                    this.dispatchEvent(new CustomEvent('editor-js-ready', {bubbles: true, composed: true}));
                    container.style.maxHeight = container.offsetHeight.toString() + 'px';
                },
                onChange: () => {}
            });
        } catch (error) {
            console.error('initializeEditor: Error creating EditorJS instance:', error);
            throw error;
        }
    }

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

    async setData(jsonData: string): Promise<void> {
        await this.editorReadyPromise;

        try {
            const data = JSON.parse(jsonData) as OutputData;

            if (!this.editor || !this.editor.blocks) {
                console.error('setData: Editor or editor.blocks not fully initialized even after promise resolved.');
                return; // místo throw, pouze logujeme a ukončíme
            }

            await this.editor.blocks.clear(); // This seems to work if no error is thrown here
            await this.editor.blocks.render(data); // <--- This is the critical line that might be failing silently

            // const editorContentAfterSet = await this.editor.save(); // odstraněno, nebylo použito
        } catch (error) {
            console.error('Error setting editor data:', error);
            throw error;
        }
    }

    public async toggleReadOnlyMode(readOnly?:boolean) {
        await this.editorReadyPromise;
        if (!this.editor) {
            throw new Error('Editor not initialized in toggleReadOnlyMode');
        }
        if (readOnly == null) {
            readOnly = !this.editor.readOnly.isEnabled;
        }
        await this.editor.readOnly.toggle(readOnly);
    }

    public async clear(){
        await this.editorReadyPromise;
        if (!this.editor) {
            throw new Error('Editor not initialized in clear');
        }
        this.editor.clear();
    }

    public async getSubChaptersNames(): Promise<{id: string, text: string}[]> {
        await this.editorReadyPromise;
        if (!this.editor) {
            throw new Error('Editor not initialized in getSubChaptersNames');
        }
        try {
            const data = await this.getData(); // This calls this.editor.save()
            if (!data || !data.blocks) {
                console.warn('getSubChaptersNames: No data or blocks found when attempting to get subchapters.');
                return [];
            }

            const filteredBlocks = data.blocks
                .filter(block => block.type === 'header' && block.data.level === 1);


            if (filteredBlocks.length === 0) {
            }

            return filteredBlocks.map(block => ({
                id: block.id || `fallback-${Math.random().toString(36).substring(2, 9)}`,
                text: block.data.text
            }));
        } catch (error) {
            console.error('Error getting subchapter names:', error);
            throw error;
        }
    }
}