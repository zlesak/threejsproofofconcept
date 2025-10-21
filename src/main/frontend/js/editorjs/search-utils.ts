//Editrojs - search functions
export async function searchInEditor(editor: any, searchText: string, editorReadyPromise: Promise<void>) {
  await editorReadyPromise;
  if (!editor || !editor.blocks) { console.error('search: editor not ready'); return; }
  clearSearchHighlights(editor);
  if (!searchText || searchText.trim() === '') return;
  const searchTextLower = searchText.toLowerCase();

  for (let i = 0; i < editor.blocks.getBlocksCount(); i++) {
    const blockElement = editor.blocks.getBlockByIndex(i)!.holder;
    const walker = document.createTreeWalker(blockElement, NodeFilter.SHOW_TEXT, null);
    let node: Node | null;
    while ((node = walker.nextNode())) {
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

function clearSearchHighlights(editor: any) {
  if (!editor || !editor.blocks) return;
  for (let i = 0; i < editor.blocks.getBlocksCount(); i++) {
    const blockElement = editor.blocks.getBlockByIndex(i)?.holder;
    if (!blockElement) continue;
    const highlightedElements = blockElement.querySelectorAll('[match="true"]');
    highlightedElements.forEach((element: Element) => {
      const parent = element.parentNode;
      if (parent) {
        parent.replaceChild(document.createTextNode(element.textContent || ''), element);
      }
    });
  }
}
