declare class TextureColorLinkTool {
    constructor(config: { data?: any; config?: any; api?: any; readOnly?: boolean });
    render(): HTMLElement;
    renderActions(): void;
    save(event: any): any;
    static setGlobalTexturesAndColors(textures: any[], colors: any[]): void;
    static get toolbox(): { title: string; icon: string };
    iconSvg(name: string, width?: number, height?: number): SVGSVGElement;
    addOption(element: HTMLSelectElement, text: string, value?: string): void;
}

export default TextureColorLinkTool;
