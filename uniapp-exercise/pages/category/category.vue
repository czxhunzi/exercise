<template>
	<view >
	  <view>
	  	<button type="primary" @click="send">发送消息</button>
		<button type="primary" @click="test">确定</button>
	  </view>
	  <view>
	      <button type="default" @click="openDatetimePicker">open picker</button>
	      <buuug7-simple-datetime-picker
	        ref="myPicker"
	        @submit="handleSubmit"
	        :start-year="2000"
	        :end-year="2099"
	        :time-init="1688860800000"
	        :time-hide="[false, false, false, true, true, true]"
	        :time-label="['年', '月', '日', '时', '分', '秒']"
	      />
	  </view>
	  <view class="uni-title uni-common-pl">时间选择器</view>
	  		<view class="uni-list">
	  			<view class="uni-list-cell">
	  				<view class="uni-list-cell-left">
	  					当前选择
	  				</view>
	  				<view class="uni-list-cell-db">
	  					<picker mode="time" :value="time" start="09:01" end="21:01" @change="bindTimeChange">
	  						<view class="uni-input">{{time}}</view>
	  					</picker>
	  				</view>
	  			</view>
	  		</view>
	</view>
	
</template>

<script>
	export default {
		data() {
			return {
				birthday: "",
				time: '12:01'
			}
		},
		created() {
			
		},
		methods: {
			send(){
				
				uni.requestSubscribeMessage({
					tmplIds: ['lnCQzBXb0Uy3FKKxTvgKPdrt5caLN1KW13KUrq2_Caw'],
					// 有意义，它要同意模板id；
					success(res){
						console.log(res);
					}
				})
			},
			notice(){
				// console.log(res);
				let user = uni.getStorageSync('user');
				uni.request({//内置的请求写法
					url:'http://localhost:9090/user/send',
					method:'POST',
					success: function(res) {
						console.log(res.data);
						console.log(user.openid);
						const pushmsg = {
									"touser": user.openid,
									"template_id": "lnCQzBXb0Uy3FKKxTvgKPdrt5caLN1KW13KUrq2_Caw",
									"data": {
										"thing1": {
											"value": "今天记得写代码"
										},
										"time2": {
											"value": "19:00"
										}
									}
								};
						console.log(pushmsg);
						uni.request({
							url: 'https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=' + res.data.data,
							data: JSON.stringify(pushmsg),
							method: 'POST',
							success: function(res) {
								console.log('success');
							}
							
						})
						
					},
					fail: function(err) {
					        console.log(err);
					}
					
				})
			},
			test(){
				let user = uni.getStorageSync('user');
				this.request({url: '/plan/sendMessage?openid=' + user.openid, method: 'POST'}).then(res => {
					if(res.code === '200'){
						console.log(res.data);
						
					}else{
						
					}
				});
			},
			 // 打开picker
			    openDatetimePicker() {
			      this.$refs.myPicker.show();
			    },
			
			    // 关闭picker
			    closeDatetimePicker() {
			      this.$refs.myPicker.hide();
			    },
			
			    handleSubmit(e) {
			      console.log(e);
			      // {year: "2023", month: "07", day: "11", hour: "15", minute: "21", seconds: '55'}
			      this.birthday = `${e.year}-${e.month}-${e.day} ${e.hour}:${e.minute}:${e.second}`;
				  console.log(this.birthday);
			    },
				 bindTimeChange: function(e) {
				    this.time = e.detail.value
					console.log(this.time);
				},
		}
	}
</script>

<style>
</style>
