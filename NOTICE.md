# 出处与许可

本仓库是 **[RfidResearchGroup/RFIDtools](https://github.com/RfidResearchGroup/RFIDtools)** 的修改版（UI 现代化重构）。

- 上游版本：v1.4.9 之后的 master，提交 `256b159`（Merge pull request #98）
- 上游作者：DXL / RFID Research Group
- 本仓库仅包含外观层改动（配色、主题、布局、drawable、依赖仓库调整、移除首页远程轮播图），
  未改动任何硬件通信、协议或功能逻辑。
- 许可：**GPL-3.0**（见 [LICENSE](LICENSE)），本修改版同样以 GPL-3.0 分发。

因上游仓库体积与网络原因，本仓库以单次提交导入，未保留上游 git 历史；
上游完整历史请见 https://github.com/RfidResearchGroup/RFIDtools

## 构建

推送到 `master` 会触发 GitHub Actions（`.github/workflows/build.yml`），
产物为该 workflow 的 `rfidtools-ui-apk` artifact。
