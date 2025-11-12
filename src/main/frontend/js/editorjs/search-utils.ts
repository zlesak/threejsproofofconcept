//Editrojs - search functions
export async function searchInEditor(editor: any, searchText: string, editorReadyPromise: Promise<void>) {
  await editorReadyPromise;
  if (!editor || !editor.blocks) { console.error('search: editor not ready'); return; }
  clearSearchHighlights(editor);
  if (!searchText || searchText.trim() === '') return;
  for (let i = 0; i < editor.blocks.getBlocksCount(); i++) {
    const blockElement = editor.blocks.getBlockByIndex(i)!.holder;
    highlightTextRecursive(blockElement, searchText);
  }
}

function highlightTextRecursive(node: Node, searchText: string) {
  if (node.nodeType === Node.TEXT_NODE) {
    const textContent = node.textContent || '';
    const regex = new RegExp(`(${searchText})`, 'gi');
    if (regex.test(textContent)) {
      const tempDiv = document.createElement('div');
      tempDiv.innerHTML = textContent.replace(regex, `<span class="editor-search-highlight" match="true">$1</span>`);
      while (tempDiv.firstChild) {
        node.parentNode?.insertBefore(tempDiv.firstChild, node);
      }
      node.parentNode?.removeChild(node);
    }
  } else if (node.nodeType === Node.ELEMENT_NODE) {
    const children = Array.from(node.childNodes);
    for (const child of children) {
      highlightTextRecursive(child, searchText);
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
        parent.normalize();
      }
    });
  }
}
