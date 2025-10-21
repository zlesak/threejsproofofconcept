import { IconLink } from '@codexteam/icons';

/**
 * TextureColorLinkTool for Editor.js
 * Allows users to select text and apply a texture and color to it via an inline toolbar resulting in a element in the content.
 * The tool uses static methods to set global textures and colors before Editor.js initialization.
 * The tool provides a button in the inline toolbar, which when clicked opens a small form with an input for text,
 * a select for textures, a select for colors (populated based on the selected texture), and a save button.
 * On save, the selected text is wrapped in an <a> element with the selected texture and color as data attributes.
 * The created <a> element with data attributes for texture and color is later used for CustomEvent fired on click.
 */
export default class TextureColorLinkTool {
  static globalTextures = [];
  static globalColors = [];

  /**
   * Static method to set global textures and colors.
   * Called before Editor.js initialization.
   * Provides static data to all instances of the tool as the data are cleared after every close of the tool.
   * @param textures
   * @param colors
   */
  static setGlobalTexturesAndColors(textures, colors) {
    TextureColorLinkTool.globalTextures = textures;
    TextureColorLinkTool.globalColors = colors;
  }

  textures = [];
  colors = [];

  /**
   * Constructor of the Tool class.
   * @param data - previously saved data
   * @param config - user config for the tool from Editor.js initialization
   * @param api - Editor.js API
   * @param readOnly - readOnly mode flag
   */
  constructor({ data, config, api, readOnly }) {
    this.toolbar = api.toolbar;
    this.inlineToolbar = api.inlineToolbar;
    this.tooltip = api.tooltip;
    this.i18n = api.i18n;
    this.config = config;

    this.nodes = {
      button: null,
      div: null,

      inputLabel: null,
      input: null,

      selectTextureLabel: null,
      selectTexture: null,

      selectColorLabel: null,
      selectColor: null,

      buttonSave: null
    };

    this.CSS = {};

    this.iconClasses = {
      base: api.styles.inlineToolButton,
      active: api.styles.inlineToolButtonActive
    };

    this.textures = TextureColorLinkTool.globalTextures;
    this.colors = TextureColorLinkTool.globalColors;
  }

  /**
   * Renders the button for the inline toolbar.
   * Called by Editor.js APIn opened inline toolbox.
   * @returns {null}
   */
  render() {
    this.nodes.button = document.createElement('button');
    this.nodes.button.type = 'button';
    this.nodes.button.classList.add(this.iconClasses.base);
    this.nodes.button.innerHTML = IconLink;
    return this.nodes.button;
  }

  /**
   * Renders actions to be displayed in the inline toolbar when the tool button is clicked.
   * Initializes the selects for textures and colors. Saves the selected ragne to apply the a element later.
   * Called by Editor.js API.
   *
   * @returns {null}
   */
  renderActions() {
    const selection = window.getSelection();
    if (selection && selection.rangeCount > 0) {
      this._selectedRange = selection.getRangeAt(0).cloneRange();
    } else {
      this._selectedRange = null;
    }

    this.nodes.div = document.createElement('div');

    this.nodes.inputLabel = document.createElement('label');
    this.nodes.input = document.createElement('input');

    if (this._selectedRange) {
      this.nodes.input.value = this._selectedRange.toString();
    } else {
      this.nodes.input.value = '';
    }

    this.nodes.selectTextureLabel = document.createElement('label');
    this.nodes.selectTexture = document.createElement('select');
    this.addOption(this.nodes.selectTexture, this.i18n.t('Vyberte texturu'), '');
    this.textures.forEach((texture) => {
      this.addOption(this.nodes.selectTexture, texture.textureName, texture.id);
    });

    this.nodes.selectColorLabel = document.createElement('label');
    this.nodes.selectColor = document.createElement('select');
    this.addOption(this.nodes.selectColor, this.i18n.t('Vyberte barvu'), '');
    this.nodes.selectTexture.addEventListener('change', () => {
      this.nodes.selectColor.innerHTML = '';
      this.addOption(this.nodes.selectColor, this.i18n.t('Vyberte barvu'), '');
      const selectedTextureId = this.nodes.selectTexture.value;
      if (selectedTextureId) {
        this.colors
          .filter((color) => color.textureId === selectedTextureId)
          .forEach((color) => {
            this.addOption(this.nodes.selectColor, color.areaName, color.hexColor);
          });
      }
    });

    const initialTextureId = this.nodes.selectTexture.value;
    if (initialTextureId) {
      this.colors
        .filter((color) => color.textureId === initialTextureId)
        .forEach((color) => {
          this.addOption(this.nodes.selectColor, color.areaName, color.hexColor);
        });
    }

    this.nodes.buttonSave = document.createElement('button');
    this.nodes.buttonSave.type = 'button';
    this.nodes.buttonSave.innerHTML = this.i18n.t('Potvrdit');
    this.nodes.buttonSave.addEventListener('click', (event) => {
      this.save(event);
    });

    this.nodes.div.appendChild(this.nodes.input);
    this.nodes.div.appendChild(this.nodes.selectTexture);
    this.nodes.div.appendChild(this.nodes.selectColor);
    this.nodes.div.appendChild(this.nodes.buttonSave);
    return this.nodes.div;
  }

  /**
   * Saves the selected text, texture, and color, then calls the surround method to apply the tool rules.
   * @param event
   */
  save(event) {
    event.preventDefault();
    event.stopPropagation();
    event.stopImmediatePropagation();

    let text = this.nodes.input.value || '';
    let texture = this.nodes.selectTexture.value || '';
    let color = this.nodes.selectColor.value || '';

    this._savedText = text;
    this._savedTexture = texture;
    this._savedColor = color;

    const range = this._selectedRange;
    this.surround(range);
  }

  /**
   * Surround range of the selected text within the block to apply the tool rules to.
   * Called by Editor.js API, that provides the range of the selected text.
   * @param range
   */
  surround(range) {
    if (range && this._savedText && this._savedTexture && this._savedColor) {
      const a = document.createElement('a');
      a.href = '#';
      a.setAttribute('data-texture-id', this._savedTexture || '');
      a.setAttribute('data-hex-color', this._savedColor || '');
      a.textContent = this._savedText || range.toString();
      a.addEventListener('click', function(event) {
        event.preventDefault();
        //TODO fire event to change the selects here?
      });
      range.deleteContents();
      range.insertNode(a);

      this._savedText = null;
      this._savedTexture = null;
      this._savedColor = null;
    }
  }

  /**
   * Helper method to add options to a select element.
   * @param element - select element
   * @param text - option text to display in the select
   * @param value - value (id for texture, color for area)
   */
  addOption(element, text, value = null) {
    let option = document.createElement('option');
    option.text = text;
    option.value = value;
    element.add(option);
  }

  /**
   * Gets the title of the tool to the Editor.js API when called.
   * @returns {string}
   */
  get title() {
    return 'Texture Color Link Tool';
  }

  /**
   * Mandatory item to tell Editor.js that this tool is inline.
   * Called automatically by Editor.js API.
   * @returns {boolean}
   */
  static get isInline() {
    return true;
  }

  /**
   * Sanitizer rules for the tool. Called by Editor.js API when adding the a element to the content data of the block.
   * @returns {{a: {href: boolean, 'data-texture-id': boolean, 'data-hex-color': boolean}}}
   */
  static get sanitize() {
    return {
      a: {
        href: true,
        'data-texture-id': true,
        'data-hex-color': true
      }
    };
  }

  /**
   * Used to clear the tool. Called by Editor.js API.
   */
  clear() {
    this.textures = [];
    this.colors = [];

    if (this.nodes.div) {
      this.nodes.div.remove();
      this.nodes.div = null;
    }
  }
}
