<template>
  <el-container class="notebook-container">
    <el-main class="editor-container">
      <el-card class="code-card">
        <div ref="editorEl" class="code-editor"/>
      </el-card>
      <el-card class="output-card">
        <div class="output-panel">
          <pre>{{ output }}</pre>
        </div>
      </el-card>
    </el-main>
    <el-footer class="toolbar">
      <el-button 
        type="primary" 
        size="large"
        @click="executeCode"
        :icon="VideoPlay"
      >
        运行代码
      </el-button>
    </el-footer>
  </el-container>
</template>

<script setup>
import { ref, onMounted,onUnmounted } from 'vue';
import { basicSetup } from 'codemirror';
import { EditorView } from '@codemirror/view';
import { python } from '@codemirror/lang-python';
import { accessHeader } from '@/net';
import { VideoPlay } from '@element-plus/icons-vue'

const output = ref('');
const editor = ref(null);
const editorEl = ref(null);

onMounted(() => {
  editor.value = new EditorView({
    doc: 'print("Hello Notebook!")\nnumbers = [1, 2, 3, 4, 5]\ntotal = sum(numbers)\nprint("列表元素的总和是:", total)',
    extensions: [basicSetup, python()],
    parent: editorEl.value
  });
});

const executeCode = async () => {
  const token = accessHeader();

  const ws = new WebSocket(`ws://localhost:8080/ws/python?token=${token.Authorization}`);
  ws.onmessage = (event) => {
    output.value += event.data + '\n';
  };
  
  ws.onopen = () => {
    ws.send(editor.value.state.doc.toString());
  };
  // Add error handling
ws.onerror = (error) => {
    output.value += `连接错误: ${error.message}\n`;
  };

  // Handle connection close
  ws.onclose = (event) => {
    if (!event.wasClean) {
      output.value += `异常断开: ${event.reason || '未知原因'}\n`;
    }
  };
};

onUnmounted(() => {
    if (ws.readyState === WebSocket.OPEN) {
      ws.close();
    }
  });

</script>

<style scoped>
.notebook-container {
  height: 100vh;
}
.editor-container {
  display: flex;
  gap: 15px;
  padding: 20px;
}
.code-card,
.output-card {
  flex: 1;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.code-editor {
  height: 65vh;
}
.output-panel {
  height: 65vh;
  padding: 10px;
  background: var(--el-bg-color-page);
  overflow: auto;
}
.toolbar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 10px 20px;
  background: var(--el-bg-color);
  border-top: 1px solid var(--el-border-color-light);
}
</style>