declare module '@editorjs/link' {
    class LinkTool {
        static get toolbox(): {
            title: string;
            icon: string;
        };

        constructor(config: any);

        render(): HTMLElement;
        save(blockContent: HTMLElement): any;
        validate(savedData: any): boolean;
        renderSettings?(): HTMLElement;
    }

    export default LinkTool;
}

declare module '@editorjs/checklist' {
    class Checklist {
        static get toolbox(): {
            title: string;
            icon: string;
        };

        constructor(config: any);

        render(): HTMLElement;
        save(blockContent: HTMLElement): any;
        validate(savedData: any): boolean;
        renderSettings?(): HTMLElement;
    }

    export default Checklist;
}

declare module '@editorjs/attaches' {
    class AttachesTool {
        static get toolbox(): {
            title: string;
            icon: string;
        };

        constructor(config: any);

        render(): HTMLElement;
        save(blockContent: HTMLElement): any;
        validate(savedData: any): boolean;
        renderSettings?(): HTMLElement;
    }

    export default AttachesTool;
}

declare module '@sotaproject/strikethrough' {
    class Strikethrough {
        static get toolbox(): {
            title: string;
            icon: string;
        };

        constructor(config: any);

        render(): HTMLElement;
        save(blockContent: HTMLElement): any;
        validate(savedData: any): boolean;
        renderSettings?(): HTMLElement;
    }

    export default Strikethrough;
}

declare module 'editorjs-hyperlink' {
    class Hyperlink {
        static get toolbox(): {
            title: string;
            icon: string;
        };

        constructor(config: any);

        render(): HTMLElement;
        save(blockContent: HTMLElement): any;
        validate(savedData: any): boolean;
        renderSettings?(): HTMLElement;
    }

    export default Hyperlink;
}

declare module '@editorjs/paragraph' {
    class Paragraph {
        static get toolbox(): {
            title: string;
            icon: string;
        };

        constructor(config: any);

        render(): HTMLElement;
        save(blockContent: HTMLElement): any;
        validate(savedData: any): boolean;
        renderSettings?(): HTMLElement;
    }

    export default Paragraph;
}
declare module '@editorjs/header' {
    class Header {
        static get toolbox(): {
            title: string;
            icon: string;
        };

        constructor(config: any);

        render(): HTMLElement;
        save(blockContent: HTMLElement): any;
        validate(savedData: any): boolean;
        renderSettings?(): HTMLElement;
    }

    export default Header;
}

declare module '@ajite/editorjs-image-base64' {
    const uploader: any;
    export default uploader;
}

declare module '@editorjs/list' {
    class List {
        static get toolbox(): {
            title: string;
            icon: string;
        };

        constructor(config: any);

        render(): HTMLElement;
        save(blockContent: HTMLElement): any;
        validate(savedData: any): boolean;
        renderSettings?(): HTMLElement;
    }

    export default List;
}
