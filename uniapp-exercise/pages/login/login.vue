<template>
	<!-- <view> -->
		<!-- <view style="width: 90%; margin: 200rpx auto;">  <!-- 200rpx  == 100px -->
		<!-- <view style=" margin-bottom: 70rpx; font-size: 50rpx; color: royalblue; text-align: center;">登 录</view> -->
		<!-- <uni-forms ref="form" :modelValue="form" :rules="rules">
			<uni-forms-item name="username">
				<uni-easyinput v-model="form.username" prefixIcon="person" placeholder="请输入登录用户名"></uni-easyinput>
			</uni-forms-item>
		
			<uni-forms-item name="password">
				<uni-easyinput type="password" v-model="form.password" prefixIcon="locked" placeholder="请输入登录密码"></uni-easyinput>
			</uni-forms-item>
		</uni-forms> -->
			
		<!-- <view>
			<button type="primary" @click="login">登 录</button>
		</view> -->
		<!-- <view>
			<button type="primary" @click="wxLogin">微 信 登 录</button>
		</view> -->
		<!-- <navigator url="/pages/register/register" style="margin: 40rpx 0; color: deepskyblue;">前往注册</navigator> -->
	<!-- </view> -->
	<!-- </view> -->
	<view class="container">
	    <view class="content">
	      <view class="center-box">
	        <button class="round-button"  @click="wxLogin">
	          <text class="button-text">微信一键登录</text>
	        </button>
	      </view>
	    </view>
	  </view>
</template>

<script>
	export default {
		data() {
			return {
				form: {username: '', password: ''},
				rules: {	
					username: {
						rules: [{required: true, errorMessage: '请输入用户名'}],
						validateTrigger:'submit'
					},
					password: {
						rules: [{required: true, errorMessage: '请输入密码'}],
						validateTrigger:'submit'
					},
				}
			}
		},
		methods: {
			login() {
				this.$refs.form.validate().then(res=>{
					this.request({url: '/user/login', method: 'POST', data: this.form}).then(res => {
						if (res.code === '200') {
							// 跳转页面 switchTab适用于在底部菜单中切换页面的情况，会关闭当前所有页面，跳转到指定页面，并显示新的底部菜单。
// navigateTo适用于一般的页面跳转，会创建一个新的页面，并保留当前页面，可以通过返回按钮回退到上一个页面。
							// uni.switchTab({
							// 	url: '/pages/index/index'
							// })
							uni.navigateTo({
								url: '/pages/information/information'
							})
							
							uni.showToast({
								title: '登录成功'
							})
							// 存储用户的数据到 storage
							uni.setStorageSync('user', res.data)
							console.log(uni.getStorageSync('user').id);
						}
					})
				}).catch(err =>{
					console.log('表单错误信息：', err);
				})
				
			},
			wxLogin(){
				uni.getUserInfo({
					desc: '登录的数据',
					success(ures) {
						console.log(ures,'返回的用户信息');
						uni.login({
							success(lres) {
								console.log(lres.code,'请求微信登录返回的数据');
								uni.request({//内置的请求写法
									url:'http://localhost:9090/user/wxLogin',
									method:'POST',
									data:{
										code: lres.code
									},
									success: function(res) {
										uni.navigateTo({
											url: '/pages/information/information'
										})
										
										uni.showToast({
											title: '登录成功'
										})
									    console.log(res.data.data);
										uni.setStorageSync('user', res.data.data);//调用别人的方法，写起来有点奇怪
									},
									fail: function(err) {
									        console.log(err);
									}
									
								})
							}
						})
					}
				})
			}
		}
	}
</script>

<style>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.content {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.center-box {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.round-button {
  width: 150px;
  height: 150px;
  border-radius: 50%; /* 设置为50%即可将按钮变成圆形 */
  background-color: #aaffff; /* 设置按钮的背景颜色 */
  color: #000; /* 设置按钮的文字颜色 */
  border: none; /* 可选，去掉按钮的边框 */
  outline: none; /* 可选，去掉按钮的点击阴影效果 */
  display: flex; /* 添加flex布局 */
  justify-content: center; /* 水平居中对齐 */
  align-items: center; /* 垂直居中对齐 */
}

.button-text {
  /* 可选，设置文字样式 */
}
</style>
