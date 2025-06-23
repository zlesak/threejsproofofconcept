import {LitElement} from 'lit';
import {customElement, state} from 'lit/decorators.js';
import EditorJS, {OutputBlockData, OutputData} from '@editorjs/editorjs';
import Header from '@editorjs/header';
import List from '@editorjs/list';
import LinkTool from '@editorjs/link';
import Paragraph from '@editorjs/paragraph';
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

    readonly editorReadyPromise: Promise<void>;
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
              .codex-editor__redactor {
                  padding-bottom: 0 !important;
              }`;
        this.appendChild(style);
    }

    async firstUpdated() {
        await this.initializeEditor();
    }

    async initializeContainer() {
        let container = document.createElement('div');
        container.id = 'editorjs';
        container.style.width = '100%';
        container.style.minHeight = '300px';
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
                    paragraph: Paragraph,
                    linkTool: LinkTool,
                    header: {
                        class: Header,
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
                            validate: false
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
                            'Table': 'Tabulka', 'Link': 'Odkaz', 'Marker': 'Zvýrazňovač',
                            'Bold': 'Tučně', 'Italic': 'Italika', 'InlineCode': 'Řádkový kód',
                            'Ordered List': 'Seřazený Seznam', 'Unordered List': 'Neseřazený Seznam',
                            'Checklist': 'Odškrtávací seznam', 'Image': 'Obrázek', 'Attaches': 'Přílohy'
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
                                "With headings": "Se záhlavím",
                                'Without headings': 'Bez záhlaví',
                                'Stretch': 'Roztáhnout',
                                'Collapse': 'Stáhnout',
                                'Add column to left': 'Přidat sloupec vlevo',
                                'Add column to right': 'Přidat sloupec vpravo',
                                'Delete column': 'Smazat sloupec',
                                'Add row above': 'Přidat řádek nahoru',
                                'Add row below': 'Přidat řádek dolu',
                                'Delete row': 'Smazat řádek'
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
                            "popover": {
                                "Filter": "Filtrovat",
                                "Nothing found": "Nic nenalezeno",
                                "Convert to": "Převést na..."
                            },
                            "toolbar": {
                                "toolbox": {
                                    "Add": "Přidat další blok",
                                }
                            },

                            "blockTunes": {
                                "toggler": {
                                    "Click to tune": "Vyberte nastavení bloku",
                                    "or drag to move": ""
                                }
                            },
                        }
                    }
                },
                onReady: () => {
                    this.resolveEditorReadyPromise();
                    this.dispatchEvent(new CustomEvent('editor-js-ready', {bubbles: true, composed: true}));
                },
                onChange: () => {
                }
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
                return;
            }

            await this.editor.blocks.clear();
            await this.editor.blocks.render(data);

        } catch (error) {
            console.error('Error setting editor data:', error);
            throw error;
        }
    }

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

    public async getSubChaptersNames(): Promise<{ id: string, text: string }[]> {
        await this.editorReadyPromise;
        if (!this.editor) {
            throw new Error('Editor not initialized in getSubChaptersNames');
        }
        try {
            const data = await this.getData();
            if (!data || !data.blocks) {
                console.warn('getSubChaptersNames: No data or blocks found when attempting to get subchapters.');
                return [];
            }

            const filteredBlocks = data.blocks
                .filter(block => block.type === 'header' && block.data.level === 1);

            return filteredBlocks.map(block => ({
                id: block.id || `fallback-${Math.random().toString(36).substring(2, 9)}`,
                text: block.data.text
            }));
        } catch (error) {
            console.error('Error getting subchapter names:', error);
            throw error;
        }
    }

    public async getSubchaptersContent(): Promise<{ h1: OutputBlockData, content: OutputBlockData[] }[]> {
        await this.editorReadyPromise;

        if (!this.editor) {
            throw new Error('Editor not initialized in getSubchaptersContent');
        }

        try {
            const data = await this.getData();
            if (!data || !data.blocks) {
                console.warn('getSubchaptersContent: No data or blocks found when attempting to get subchapters content.');
                return [];
            }

            const result: { h1: OutputBlockData, content: OutputBlockData[] }[] = [];
            let oldH1Text: OutputBlockData | null = null;
            let currentH1Text: OutputBlockData | null = null;
            let subchapterData: OutputBlockData[] = [];

            data.blocks.forEach((block, index) => {
                if (block.type === "header" && block.data.level === 1) {
                    oldH1Text = currentH1Text;
                    currentH1Text = block
                    if (subchapterData.length != 0 && oldH1Text != null) {
                        result.push({
                            h1: oldH1Text,
                            content: subchapterData
                        });
                    } else if (oldH1Text != null) {
                        result.push({
                            h1: oldH1Text,
                            content: []
                        });
                    } else if(index > 0) {
                        let noHeader: OutputBlockData = {
                            id: `fallback-${Math.random().toString(36).substring(2, 9)}`,
                            type: "header",
                            data: {
                                "text": "Obsah bez hlavního nadpisu",
                                "level": 1
                            }
                        };
                        result.push({
                            h1: noHeader,
                            content: subchapterData
                        });
                    }
                    subchapterData = [];

                    if(index == data.blocks.length - 1) {
                        result.push({
                            h1: currentH1Text,
                            content: []
                        });
                    }
                } else {
                    if (block.type === "header"){
                        subchapterData.push(block);
                    }
                }
            });
            return result;
        } catch (error) {
            console.error('Error getting subchapters content:', error);
            throw error;
        }
    }
}