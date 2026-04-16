# 主机管理 CRUD 验证记录

日期：2026-04-16

## 范围

- 主机列表加载
- 新增主机
- 编辑主机
- 删除主机
- 单个连通性检测
- 批量导入虚拟数据
- 演示数据模式下的本地存储 CRUD

## 验证命令

### 1. 前端构建

```powershell
cd aomp-admin
npm run build
```

结果：通过。

### 2. 演示数据 CRUD 脚本验证

```powershell
cd aomp-admin
@'
import {
  resetMockHosts,
  listMockHosts,
  createMockHost,
  updateMockHost,
  deleteMockHost,
  checkMockHostConnectivity,
  importMockHosts,
  createImportedDemoHosts
} from './src/utils/hostMockStore.js'

resetMockHosts()
const initialTotal = listMockHosts({ pageNum: 1, pageSize: 50 }).total
const created = createMockHost({
  hostname: 'crud-check-01',
  alias: 'CRUD验证节点',
  ipAddress: '172.20.30.41',
  sshPort: 22,
  osType: 'Ubuntu 22.04',
  sshUser: 'root',
  sshAuthType: 'password',
  remark: 'created in verification'
})
updateMockHost(created.id, {
  hostname: 'crud-check-01-updated',
  alias: 'CRUD验证节点-已编辑',
  sshPort: 2222,
  osType: 'Rocky Linux 9',
  sshUser: 'ops',
  sshAuthType: 'password',
  remark: 'updated in verification'
})
checkMockHostConnectivity(created.id)
importMockHosts(createImportedDemoHosts())
deleteMockHost(created.id)
const finalResult = listMockHosts({ pageNum: 1, pageSize: 100 })
console.log(JSON.stringify({
  initialTotal,
  finalTotal: finalResult.total,
  importedExists: finalResult.records.some(item => item.hostname === 'demo-import-01'),
  deletedMissing: !finalResult.records.some(item => item.id === created.id)
}, null, 2))
'@ | node --input-type=module -
```

结果：

```json
{
  "initialTotal": 5,
  "finalTotal": 8,
  "importedExists": true,
  "deletedMissing": true
}
```

## 结论

- 演示数据模式下，主机新增、编辑、删除、导入、连通性检测流程可用。
- 主机管理页面已具备用于联调前演示和自测的本地虚拟数据能力。
