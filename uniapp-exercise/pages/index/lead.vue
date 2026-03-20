<template>
  <view>
      <view v-for="(item, index) in day" :key="index" class="item">
		  <uni-data-select class="left" v-model="item.modeId" :localdata="range" :disabled="item.id"></uni-data-select>
		  <uni-number-box v-model="item.time" class="right"></uni-number-box>
		  <text>（分钟）</text>
      </view>
	 <button type="success" open-type="getUserInfo" @getuserinfo="getStep">获取微信运动信息</button>
	 <button @click="addNewDay">添加运动</button>
	 <button type="primary" @click="put">确定</button>
  </view>
</template>
<script>
import  WXBizDataCrypt from '../../utils/WXBizDataCrypt.js'
	export default {
	  data() {
	    return {
			day: [],
			id: '',
			sessionKey: '',
			appid: '',
			range: [],
	    };
	  },
	  created() {
	  	this.load();
	  },
	  methods: {
		load(){
			this.id = uni.getStorageSync('user').id;
			this.request({url: '/consume/getToday?userId=' + this.id, method: 'GET'}).then(res => {
				if(res.code === '200'){
					console.log(res.data);
					this.day = res.data;
					
				}else{
					
				}
			});
			this.request({url: '/mode/list', method: 'GET'}).then(res => {
				if(res.code === '200'){
					this.range = res.data.map(item => ({value: item.id, text: item.name}));
					
					console.log(this.range);
				}else{
					
				}
			});
		},
		put(){
			console.log(this.day);
			this.request({url: '/consume/change', method: 'POST', data: this.day}).then(res => {
				if(res.code === '200'){
				}else{
					
				}
			});
		},
		addNewDay() {
		    this.day.push({
			  id: '',
			  userId: this.id,
		      mode: '',
		      time: 0,
			  createtime: '',
		    });
		},
		getStep() {
			var that = this;
			// success回调函数中使用that来引用组件实例的this，这通常是为了解决回调函数内部上下文丢失的问题，使用this会报错
			uni.login({
				success(lres) {
					console.log(lres.code,'请求微信登录返回的数据');
					uni.request({//内置的请求写法
						url: uni.baseUrl + 'user/wxLogin',
						method:'POST',
						data:{
							code: lres.code
						},
						success: function(res) {
							
						    console.log(res.data.data);
							console.log(res.data.data.appid);
							that.sessionKey = res.data.data.session_key;
							that.appid = res.data.appid;
							that.getStepInfo(res.data.data.appid,res.data.data.session_key);
						},
						fail: function(err) {
						        console.log(err);
						}
						
					})
				}
			})
		},
		getStepInfo(appid, session_key){
			var that = this;
			uni.getSetting({
					success: function(res) {
						console.log(res);
						// 未开启微信运动授权
						if (!res.authSetting['scope.werun']) {
							uni.showModal({
								title: '提示',
								content: '获取微信运动步数，需要开启计步权限',
								success: function(res) {
									if (res.confirm) {
										//跳转去设置
										wx.openSetting({
											success: function(res) {}
										})
									} else {
										//不设置
									}
								}
							})
						} else {
							uni.getWeRunData({
								success: function(res) {
									var encryptedData = res.encryptedData;
									var iv = res.iv;
									var pc = new WXBizDataCrypt(appid, session_key);
									var walkData = pc.decryptData(encryptedData, iv);
									const walkArray = walkData.stepInfoList.map(walk => walk.step);
									console.log(walkArray);
									const totalData = {
										userId: that.id,
										dataArray: walkArray
									};
									that.request({url: '/consume/walk', method: 'POST', data: totalData}).then(res => {
										if(res.code === '200'){
											  that.load();
										}else{
											
										}
									});
									console.log(walkData.stepInfoList[30])
								},
								fail: function(res) {
									wx.showModal({
										title: '提示',
										content: '开发者未开通微信运动，请关注“微信运动”公众号后重试',
										showCancel: false,
										confirmText: '知道了'
									})
								}
							})
						}
					}
				})
		}
	  },
	};
</script>
<style>
	.item {
	  display: flex;
	  margin-bottom: 16px;
	}
	
	.left {
	  flex: 1;
	}
	
	.right {
	  flex: 1;
	}
</style>
