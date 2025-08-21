import { IconLink } from '@codexteam/icons';

export default class TextureColorLinkTool {
  static globalTextures = [];
  static globalColors = [];

  static setGlobalTexturesAndColors(textures, colors) {
    TextureColorLinkTool.globalTextures = textures;
    TextureColorLinkTool.globalColors = colors;
  }

  textures = [];
  colors = [];

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


  render() {
    this.nodes.button = document.createElement('button');
    this.nodes.button.type = 'button';
    this.nodes.button.classList.add(this.iconClasses.base);
    this.nodes.button.innerHTML = IconLink;
    return this.nodes.button;
  }

  renderActions() {
    this.nodes.div = document.createElement('div');

    this.nodes.inputLabel = document.createElement('label');
    this.nodes.input = document.createElement('input');

    this.nodes.selectTextureLabel = document.createElement('label');
    this.nodes.selectTexture = document.createElement('select');
    this.addOption(this.nodes.selectTexture, this.i18n.t('Vyberte texturu'), '');
    this.textures.forEach((texture) => {
      console.log(texture, texture.name, texture.id);
      this.addOption(this.nodes.selectTexture, texture.name, texture.id);
    });

    this.nodes.selectColorLabel = document.createElement('label');
    this.nodes.selectColor = document.createElement('select');
    this.addOption(this.nodes.selectColor, this.i18n.t('Vyberte barvu'), '');
    this.colors.forEach((color) => {
      this.addOption(this.nodes.selectColor, color.name, color.id);
    });

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

  save(event) {
    event.preventDefault();
    event.stopPropagation();
    event.stopImmediatePropagation();

    let text = this.nodes.input.value || '';
    let texture = this.nodes.selectTexture.value || '';
    let color = this.nodes.selectColor.value || '';

    console.log(text, texture, color);
  }

  addOption(element, text, value = null) {
    let option = document.createElement('option');
    option.text = text;
    option.value = value;
    element.add(option);
  }


  surround(range) {
    if (range) {

    }
  }

  get title() {
    return 'Texture Color Link Tool';
  }

  static get isInline() {
    return true;
  }

  static get sanitize() {
    return {
      a: {
        href: true,
        target: true,
        rel: true
      }
    };
  }

  clear() {
    this.textures = [];
    this.colors = [];

    if (this.nodes.div) {
      this.nodes.div.remove();
      this.nodes.div = null;
    }
  }
}
