<template>
	<div>
		<div class="title"
			style="background-color: green; color: white; text-align: center; padding: 10px; border-radius: 10px;width: 75vw;">
			玉米去雄情况检测
		</div>
		<div class="outer-container"
			style="display: flex; justify-content: center; align-items: flex-start; border: 3px solid green;width: 75vw;">
			<div>
				<!-- 这是一个隐藏的文件输入框，用于选择图片。它设置为隐藏，通过后面的按钮或其他交互来触发显示 -->
				<input v-show="false" type="file" accept="image/*" @change="tirggerFile($event)" ref="input" />
				<div style="width:30vw;height:23vw;border:1px solid;text-align:center;margin-top: 2vw">
					<span v-if="imgUrl==''">未上传图片</span>
					<img style="height:100%;width:100%;" v-if="imgUrl!=''" :src="imgUrl" />
				</div>
				<el-button @click="openImg"
					style="width: 30vw;margin-bottom: 2vw;background-color: green; color: white;">点击上传</el-button>
			</div>
			<div>
				<div style="width:30vw;height:23vw;border:1px solid;text-align:center;margin-left: 5vw;margin-top: 2vw">
					<span v-if="predictedImgUrl==''">未进行预测</span>
					<img v-if="predictedImgUrl" :src="predictedImgUrl" style="width: 30vw; height: 23vw;">
				</div>
				<el-button @click="callJavaFunction"
					style="width: 30vw; margin-left: 5vw;margin-bottom: 2vw;background-color: green; color: white;">
					开始预测</el-button>
			</div>
		</div>

		<div class="out"
			style="display: flex; justify-content: center; align-items: flex-start;width: 75vw;margin-top: 1vw;">
			<div class="title_CAM"
				style="background-color: steelblue; color: white; text-align: center; padding: 10px; border-radius: 10px;width: 24vw;">
				图像采集
			</div>
			<div class="title_information_0"
				style="background-color: steelblue; color: white; text-align: center; padding: 10px; border-radius: 10px;width: 24vw;margin-left: 2vw;">
				第一区域环境信息
			</div>
			<div class="title_information_1"
				style="background-color: steelblue; color: white; text-align: center; padding: 10px; border-radius: 10px;width: 24vw;margin-left: 1vw;">
				第二区域环境信息
			</div>
		</div>
		<div class="out"
			style="display: flex; justify-content: center; align-items: flex-start;width: 75vw;margin-top: 1vw;">
			<div class="information"
				style="display: flex; flex-direction: column; border: 3px solid steelblue;width: 24vw;">
				<div style="display: flex; align-items: center;margin-top: 10px;margin-bottom: 5px;margin-left: 20px;">
					<span>摄像头接入IP：</span>
					<span>{{CAM_IP}}</span>
				</div>
				<div style="display: flex; align-items: center;margin-top: 5px;margin-bottom: 5px;margin-left: 20px;">
					<span>摄像服务开关：</span>
					<el-switch v-model="CAM_key" active-color="#13ce66" inactive-color="#ff4949"
						@change="down_CAM_IP"></el-switch>
					<div v-if="loading" class="loading">加载中...</div>
				</div>
				<div style="display: flex; align-items: center;margin-top: 5px;margin-bottom: 10px;margin-left: 20px;">
					<el-button @click="visitCameraPage" :disabled="loading"
						style="background-color: steelblue;color: white;">访问摄像头页面</el-button>
				</div>
			</div>
			<div class="information"
				style="display: flex; flex-direction: column; align-items: flex-start; border: 3px solid steelblue;width: 24vw;margin-left: 2vw;">
				<div style="display: flex; align-items: center;margin-top: 10px;margin-bottom: 5px;margin-left: 20px;">
					<span>终端：{}</span>
				</div>
				<div style="display: flex; align-items: center;margin-top: 5px;margin-bottom: 5px;margin-left: 20px;">
					<span>温度：{}度</span>
					<span>湿度：{}%</span>
				</div>
				<div style="display: flex; align-items: center;margin-top: 5px;margin-bottom: 10px;margin-left: 20px;">
					<span>土壤湿度：{}%</span>
					<span>光照：{}Lux</span>
				</div>
			</div>
			<div class="information"
				style="display: flex; flex-direction: column; align-items: flex-start; border: 3px solid steelblue;width: 24vw;margin-left: 1vw;">
				<div style="display: flex; align-items: center;margin-top: 10px;margin-bottom: 5px;margin-left: 20px;">
					<span>终端：{}</span>
				</div>
				<div style="display: flex; align-items: center;margin-top: 5px;margin-bottom: 5px;margin-left: 20px;">
					<span>温度：{}度</span>
					<span>湿度：{}%</span>
				</div>
				<div style="display: flex; align-items: center;margin-top: 5px;margin-bottom: 10px;margin-left: 20px;">
					<span>土壤湿度：{}%</span>
					<span>光照：{}Lux</span>
				</div>
			</div>
		</div>


	</div>
</template>

<script>
	import axios from 'axios';
	import {
		MessageBox
	} from 'element-ui';
	export default {
		name: "DataCente",
		data() {
			return {
				product_id: '5ge5BdeW12',
				device_name: 'LoRa_cam',
				token: 'version=2018-10-31&res=products%2F5ge5BdeW12%2Fdevices%2FLoRa_cam&et=1923796800&method=md5&sign=Pp%2FOcIi5OlobEB1oBkkD7w%3D%3D', //定义token变量，存储函数返回值的token值
				CAM_key: false,
				CAM_IP: '',
				timer: null, //用于保证信息获取进程唯一
				loading: false,
				CAM_http: 'http://',

				//图片上传
				imgUrl: '',
				isSelectFile: false,
				maxFileSize: 10 * 1024 * 1024, // 最大文件大小，2MB

				//图片预测
				predictedImgUrl: ''
			}
		},

		//保证信息获取进程唯一
		beforeDestroy() {
			if (this.timer) {
				clearInterval(this.timer);
				this.timer = null;
			}
		},
		mounted() {
			if (!this.timer) {
				this.timer = setInterval(() => {
					this.fetchData();
				}, 6000);
			}
		},

		methods: {
			fetchData() {
				axios.get('https://iot-api.heclouds.com/thingmodel/query-device-property', {
					params: {
						product_id: '5ge5BdeW12',
						device_name: 'LoRa_cam'
					},
					headers: {
						"authorization": this.token
					},
				}).then((res) => {
					this.CAM_IP = res.data.data[0].value;
					console.log(this.CAM_IP);
				}).catch((error) => {
					console.error(error);
				});
			},

			down_CAM_IP(event) {
				this.loading = true;
				setTimeout(() => {
					// 根据开关当前状态设置要发送的设备属性值
					this.CAM_key = event; //event.detail.value;
					console.log(this.CAM_key);
					axios.post('https://iot-api.heclouds.com/thingmodel/set-device-property', {
						product_id: this.product_id,
						device_name: this.device_name,
						params: {
							CAM_key: this.CAM_key
						}
					}, {
						headers: {
							authorization: this.token // 替换为你的Authorization值
						}
					}).then(() => {
						if (this.CAM_key) {
							MessageBox.alert('摄像服务已开启', '提示', {
								confirmButtonText: '确定'
							});
						} else {
							MessageBox.alert('摄像服务已关闭', '提示', {
								confirmButtonText: '确定'
							});
						}
						this.loading = false;
						// CAM_http = CAM_http+CAM_IP
						// console.log(CAM_http);
						// window.location.href = CAM_http;
					}).catch((error) => {
						console.error(error);
						this.loading = false;
					});
				}, 2000);
			},

			visitCameraPage() {
				if (this.CAM_IP) {
					const targetUrl = this.CAM_http + this.CAM_IP;
					window.location.href = targetUrl;
				} else {
					MessageBox.alert('摄像头IP未获取到，请稍后再试', '提示', {
						confirmButtonText: '确定'
					});
				}
			},
			//图片上传
			tirggerFile: function(event) {
				let file = event.target.files[0];
				const isImage = file.type.match(/image\/(png|jpg|jpeg)/);
				if (!isImage) {
					MessageBox.alert('请选择正确的图片格式', '提示', {
						confirmButtonText: '确定'
					});
					return;
				}
				if (file.size > this.maxFileSize) {
					MessageBox.alert('文件大小超过限制，请选择小于 10MB 的图片', '提示', {
						confirmButtonText: '确定'
					});
					return;
				}
				let url = "";
				var reader = new FileReader();
				reader.readAsDataURL(file);
				let that = this;
				reader.onload = function(e) {
					url = this.result.substring(this.result.indexOf(",") + 1);
					that.imgUrl = "data:image/png;base64," + url;
				};
				this.uploadFile(file);
			},
			openImg() {
				this.$refs.input.click();
			},
			uploadFile(file) {
				console.log(file);
				const formData = new FormData();
				formData.append('image', file);
				console.log('FormData content:', formData);
				axios.post('http://localhost:8181/user/uploadImage', formData, {
					headers: {
						'Content-Type': 'multipart/form-data'
					}
				}).then(response => {
					MessageBox.alert('图片上传成功', '提示', {
						confirmButtonText: '确定'
					});
				}).catch(error => {
					console.error('图片上传失败:', error);
					MessageBox.alert('图片上传失败，请重试', '错误提示', {
						confirmButtonText: '确定'
					});
				});
			},

			callJavaFunction() {
				axios.get('http://localhost:8181/user/getPredictedImage', {
						responseType: 'blob'
					})
					.then((res) => {
						const reader = new FileReader();
						reader.onload = function(e) {
							this.predictedImgUrl = e.target.result;
						}.bind(this);
						reader.readAsDataURL(res.data);
					})
					.catch((error) => {
						console.error('获取预测图片出错:', error);
					});
			}


		}
	};
</script>

<style scoped>
	.title {
		display: flex;
		justify-content: center;
		align-items: flex-start;
		margin: auto;
	}

	.outer-container {
		display: flex;
		justify-content: center;
		align-items: flex-start;
		border: 1px solid lightgreen;
		width: 75vw;
		box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
		border-radius: 10px;
		background-color: white;
		transform: translateY(-2px);
		margin: auto;
	}

	.out {
		display: flex;
		justify-content: center;
		align-items: flex-start;
		margin: auto;
	}

	.information {
		display: flex;
		justify-content: center;
		align-items: flex-start;
		border: 1px solid steelblue;
		width: 24vw;
		box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
		border-radius: 10px;
		background-color: white;
		transform: translateY(-2px);
		margin: auto;
	}

	body {
		display: flex;
		justify-content: center;
		min-height: 100vh;
		margin: 0;
	}
</style>
<!--<template>
	<div>
		<h1>这是数据中心页面</h1>
	</div>
</template>
<script>
import axios from 'axios';

export default {
  name: "DataCenter",
  mounted() {
    // 在组件挂载时调用获取数据的函数
    this.fetchData();
  },
  methods: {
    fetchData() {
      axios.get('http://localhost:8181/user/messages')
      .then((response) => {
          // 成功获取数据后打印数据
          console.log(response.data);
        })
      .catch((error) => {
          // 如果获取数据出错，打印错误信息
          console.log('获取数据出错：', error);
        });
    }
  }
}
</script>

<style scoped>
</style> -->