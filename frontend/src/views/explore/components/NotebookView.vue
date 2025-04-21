<template>
  <div class="notebook-container">
    <div ref="editorEl" class="code-editor"></div>
    <div class="output-panel">
      <pre>{{ output }}</pre>
    </div>
    <div class="toolbar">
      <button @click="executeCode">运行</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { basicSetup } from 'codemirror';
import { EditorView } from '@codemirror/view';
import { python } from '@codemirror/lang-python';

const output = ref('');
const editor = ref(null);
const editorEl = ref(null);

onMounted(() => {
  editor.value = new EditorView({
    doc: 'print("Hello Notebook!")',
    extensions: [basicSetup, python()],
    parent: editorEl.value
  });
});

const executeCode = async () => {
  const ws = new WebSocket('ws://localhost:8079/ws/python');
  ws.onmessage = (event) => {
    output.value += event.data + '\n';
  };
  ws.onopen = () => {
    ws.send(editor.value.state.doc.toString());
  };
};
</script>

<style scoped>
.notebook-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding: 20px;
}
.code-editor {
  flex: 2;
  border: 1px solid #ccc;
  margin-bottom: 10px;
}
.output-panel {
  flex: 1;
  padding: 10px;
  background: #f5f5f5;
  border: 1px solid #ddd;
  overflow: auto;
}
button {
  padding: 8px 16px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
</style>