<template>
  <div id="globalUploader" class="global-uploader">
    <!-- 上传文件组件 -->
	<!-- :key="uploaderKey" -->
    <uploader
      ref="uploader"
      :options="options"
      
      :autoStart="false"
      :fileStatusText="fileStatusText"
      @files-added="handleFilesAdded"
      @file-success="handleFileSuccess"
      @file-error="handleFileError"
	  @file-removed="handleFileRemoved"
    >
      <uploader-unsupport></uploader-unsupport>
      <!-- 选择按钮 在这里隐藏 -->
      <uploader-btn class="select-file-btn" :attrs="attrs" ref="uploadBtn"
        >选择文件</uploader-btn
      >
      <uploader-btn
			class="select-file-btn"
			:attrs="attrs"
			:directory="true"
			ref="uploadDirBtn"
			>选择目录</uploader-btn
		>
		<!-- 拖拽上传 -->
		<!-- <uploader-drop
			class="drop-box"
			id="dropBox"
			@paste.native="handlePaste"
			v-show="dropBoxShow"
		>
			<div class="paste-img-wrapper" v-show="pasteImg.src">
				<div class="paste-name">{{ pasteImg.name }}</div>
				<img
					class="paste-img"
					:src="pasteImg.src"
					:alt="pasteImg.name"
					v-if="pasteImg.src"
				/>
			</div>
			<span class="text" v-show="!pasteImg.src">
				截图粘贴或将文件拖拽至此区域上传
			</span>
			<i
				class="upload-icon el-icon-upload"
				v-show="pasteImg.src"
				@click="handleUploadPasteImg"
				>上传图片</i
			>
			<i
				class="delete-icon el-icon-delete"
				v-show="pasteImg.src"
				@click="handleDeletePasteImg"
				>删除图片</i
			>
			<i class="close-icon el-icon-circle-close" @click="dropBoxShow = false"
				>关闭</i
			>
		</uploader-drop> -->
		<!-- 上传列表 -->
		<uploader-list v-show="panelShow">
			<template v-slot:default="props">
				<div class="file-panel">
					<div class="file-title">
						<span class="title-span">
							上传列表<span class="count"
								>（{{ props.fileList.length }}）</span
							>
						</span>
						<div class="operate">
							<el-button
								type="text"
								:title="collapse ? '展开' : '折叠'"
								:icon="collapse ? 'el-icon-full-screen' : 'el-icon-minus'"
								@click="collapse ? (collapse = false) : (collapse = true)"
							>
							</el-button>
							<el-button
								@click="handleClosePanel"
								type="text"
								title="关闭"
								icon="el-icon-close"
							>
							</el-button>
						</div>
					</div>
					<!-- 正在上传的文件列表 -->
					<el-collapse-transition>
						<ul class="file-list" v-show="!collapse">
							<li
								class="file-item"
								:class="{ 'custom-status-item': file.statusStr !== '' }"
								v-for="file in props.fileList"
								:key="file.id"
							>
								<uploader-file
									ref="fileItem"
									:file="file"
									:list="true"
								></uploader-file>
								<!-- 自定义状态 -->
								<span class="custom-status">{{ file.statusStr }}</span>
							</li>
							<div class="no-file" v-if="!props.fileList.length">
								<i class="icon-empty-file"></i> 暂无待上传文件
							</div>
						</ul>
					</el-collapse-transition>
					<span style="display: none;">{{ statusStr }}</span>
				</div>
			</template>
		</uploader-list>
      </uploader>
  </div>
</template>

<script>
import SparkMD5 from 'spark-md5'
import axios from 'axios'
export default {
  name: 'globalUploader',
  data () {
    return {
		// uploaderKey: new Date().getTime(),
      // 上传组件配置项
		options: {
			// target: process.env.VUE_APP_BASE_API + '/file/upload', // 上传文件-目标 URL
			target: function (file, chunkFile, mode) {
				return process.env.VUE_APP_BASE_API + '/file/createMultipartUpload';
			},
			chunkSize: 5 * 1024 * 1024, //  每个分片的大小
			fileParameterName: 'file', //  上传文件时文件的参数名，默认 file
			maxChunkRetries: 3, //  并发上传数，默认 3
			testChunks: true, //  是否开启分片已存在于服务器的校验
			// 是否强制所有的块都是小于等于 chunkSize 的值
			forceChunkSize: true,
			// 服务器分片校验函数，秒传及断点续传基础
			checkChunkUploadedByResponse: function (chunk, message) {
				
				let objMessage = JSON.parse(message)
				// console.log(objMessage)
				if (objMessage.code == 200) {
					let data = objMessage.data
					// if (data.needMerge) {
					// 	chunk.file.
					// }
					if (data.skipUpload) {
						// 分片已存在于服务器中
						return true
					}
					if (objMessage.data.uploadId) {
						chunk.file.uploadId = objMessage.data.uploadId
					}
					if (objMessage.data.taskId) {
						chunk.file.taskId = objMessage.data.taskId
					}
					// console.log((data.uploaded || []).indexOf(chunk.offset + 1) >= 0)
					return (data.uploaded || []).indexOf(chunk.offset + 1) >= 0
				} else {
					return true
				}
			},
			query() {}
		},
      // 文件状态文案映射
		fileStatusText: {
			success: '上传成功',
			error: 'error',
			uploading: '上传中',
			paused: '暂停中',
			waiting: '等待中',
			waitForMd5: '正在效验MD5'
		},
        attrs: {
			accept: '*'
		},
		params: {
			bucketName: 'other',
			fileName: 'this.filename'
		},
		filesLength: 0, //  上传的文件个数
		panelShow: false, //  上传文件面板是否显示
		collapse: false, //	上传文件面板是否折叠
		dropBoxShow: false, //  拖拽上传是否显示
		statusStr: ''
    }
  },
  created () {

  },
  computed: {
    // Uploader	上传组件实例
		uploaderInstance() {
			return this.$refs.uploader.uploader
		}
  },
  mounted: function (){
	this.$root.Bus.$on('openUploader', (uploadType) => {
        this.openFileUploader(uploadType)
    })
	this.$root.Bus.$on('resetUploaderKey', () => {
        this.uploaderKey = new Date().getTime()
    })
  },
  methods: {
	openFileUploader(uploadType) {
		switch (uploadType) {
			case 1: {
				this.$refs.uploadBtn.$el.click()
				break
			}
			case 2: {
				this.$refs.uploadDirBtn.$el.click()
				break
			}
		}
    },
    handleFilesAdded(files) {
		// 批量选择的文件的总体大小
		const filesTotalSize = files
			.map((item) => item.size)
			.reduce((pre, next) => {
				return pre + next
			}, 0)
		this.filesLength += files.length
		files.forEach((file) => {
			this.dropBoxShow = false
			this.panelShow = true
			this.collapse = false
			this.computeMD5(file)
		})
	},
	/**
	 * 文件上传成功 回调函数
	 * @param {object} rootFile 成功上传的文件所属的根 Uploader.File 对象，它应该包含或者等于成功上传文件
	 * @param {object} file 当前成功的 Uploader.File 对象本身
	 * @param {string} response 服务端响应内容，永远都是字符串
	 */
	handleFileSuccess(rootFile, file, response) {
		if (response == '') {
			file.statusStr = '上传失败'
			this.statusStr = '上传失败'
			return
		}
		console.log('handleFileSuccess')
		let result = JSON.parse(response)
		if (result.code == 200) {
			file.statusStr = ''
			this.statusStr = ''
			// console.log('handleFileSuccess file:')
			// console.log(file)
			if (result.data.needMerge === true) {
				axios({
				method: 'get',
				url: process.env.VUE_APP_BASE_API + '/file/completeMultipartUpload',
				params: {
					uploadId: file.uploadId,
					chunkNum: file.chunks.length,
					taskId: file.taskId
				}
				}).then((res) => {
					if (res.data === true) {
						this.$root.Bus.$emit('getTableList', {})
						this.$message.success(file.name + `上传完毕`)
					} else {
						this.$message.success(file.name + `上传失败`)
					}
				}).error((res) => {
					file.statusStr = '上传失败'
					this.statusStr = '上传失败'
					this.$message.success(file.name + `上传失败`)
				})
			} else {
				this.$root.Bus.$emit('getTableList', {})
				this.$message.success(file.name + `上传完毕`)
			}
		} else {
			this.$message.error(result.msg)
			file.statusStr = '上传失败'
			this.statusStr = '上传失败'
		}
		this.filesLength--
	},
	/**
	 * 文件移除 回调函数
	 */
	handleFileRemoved(file) {
		this.filesLength--
		// console.log(`文件: ${file.name} handleFileRemoved----------------------------------------`)
		// console.log(file)
	},
	/**
	 * 文件上传失败 回调函数
	 * @param {object} rootFile 成功上传的文件所属的根 Uploader.File 对象，它应该包含或者等于成功上传文件
	 * @param {object} file 当前成功的 Uploader.File 对象本身
	 * @param {string} response 服务端响应内容，永远都是字符串
	 */
	handleFileError(rootFile, file, response) {
		this.$message({
			message: response,
			type: 'error'
		})
	},
	/**
	 * 计算md5，实现断点续传及秒传
	 * @param {object} file 文件信息
	 */
	computeMD5(file) {
		console.log('uploaderInstance')
		console.log(file)
		let fileReader = new FileReader()
		let blobSlice =
			File.prototype.slice ||
			File.prototype.mozSlice ||
			File.prototype.webkitSlice
		let currentChunk = 0
		const chunkSize = 1 * 1024 * 1024
		let chunks = Math.ceil(file.size / chunkSize)
		let spark = new SparkMD5.ArrayBuffer()
		// 文件状态设为"计算MD5"
		file.statusStr = '计算MD5'
		this.statusStr = '计算MD5'
		file.waitForMd5 = true
		file.pause()
		loadNext()
		fileReader.onload = (e) => {
			spark.append(e.target.result)
			if (currentChunk < chunks) {
				currentChunk++
				// 实时展示MD5的计算进度
				file.statusStr = `校验MD5 ${((currentChunk / chunks) * 100).toFixed(
					0
				)}%`
				this.statusStr = `校验MD5 ${((currentChunk / chunks) * 100).toFixed(
					0
				)}%`
				loadNext()
			} else {
				let md5 = spark.end()
				this.calculateFileMD5End(md5, file)
			}
		}
		fileReader.onerror = function () {
			this.$notify({
				title: '错误',
				message: `文件${file.name}读取出错，请检查该文件`,
				type: 'error',
				duration: 2000
			})
			file.cancel()
		}
		function loadNext() {
			let start = currentChunk * chunkSize
			let end = start + chunkSize >= file.size ? file.size : start + chunkSize
			fileReader.readAsArrayBuffer(blobSlice.call(file.file, start, end))
		}
	},
	/**
	 * 文件MD5计算结束
	 * @param {string} md5 文件 MD5 值
	 * @param {object} file 文件对象
	 */
	calculateFileMD5End(md5, file) {
		const params = {
			bucketName: 'other',
			fileName: file.name,
		}
		// 将自定义参数直接加载uploader实例的opts上
		Object.assign(this.uploaderInstance.opts, {
			query: {
				...params
			}
		})
		file.uniqueIdentifier = md5
		// 移除自定义状态
		file.statusStr = ''
		this.statusStr = ''
		file.waitForMd5 = false
		file.resume()
	},
	/**
	 * 关闭上传面板，并停止上传
	 */
	handleClosePanel() {
		this.uploaderInstance.cancel()
		this.panelShow = false
	},
  }
}
</script>

<style lang="scss" scoped>
.global-uploader {
  position: absolute;
  z-index: 20;
  right: 15px;
  bottom: 15px;
  width: 750px;
  .file-panel {
    background-color: #fff;
    border: 1px solid #e2e2e2;
    border-radius: 10px 10px 0 0;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
	}
  .file-title {
	margin: 0px;
	padding-left: 0;
	border-bottom-left-radius: 2px;
	border-bottom-right-radius: 2px;
	.title-span {
        padding-left: 0;
        font-size: 16px;
	}
    .operate {
        text-align: right;
	}
  }
  .file-list {
      position: relative;
      height: 240px;
      overflow-x: hidden;
      overflow-y: auto;
      background-color: #fff;
      font-size: 12px;
      list-style: none;
    //   setScrollbar(6px, #EBEEF5, #C0C4CC);

      .file-item {
		border: 0.5px solid #000103;
        position: relative;
        background-color: #fff;

        .uploader-file::v-deep {
          height: 40px;
          line-height: 40px;

          .uploader-file-progress {
            border: 1px solid;
            border-right: none;
            border-left: none;
            background-color: #aaee88;
          }
          .uploader-file-name {
			text-align: left;
            width: 35%;
          }

          .uploader-file-size {
            width: 15%;
          }

          .uploader-file-meta {
            display: none;
          }

          .uploader-file-status {
            width: 40%;
			left: 0;
            text-indent: 0;
          }

          .uploader-file-actions>span {
            margin-top: 12px;
          }
        }

        .uploader-file[status='success']::v-deep {
          .uploader-file-progress {
            border: none;
          }
        }

		.uploader-file[status="waitForMd5"]::v-deep {
			.uploader-file-resume {
				display: none;
			}
		}
      }

      .file-item {
	  	.custom-status-item {
			.uploader-file-status::v-deep{
				visibility: hidden;
				display: none;
			}
		}

        .custom-status {
          top: 50%;
          right: 20%;
          width: 24%;
          height: 40px;
          line-height: 0;
        }
      }
    }
}


/* 隐藏上传按钮 */
.select-file-btn {
  display: none;
}
</style>