<template>
	<view>
		<uni-forms :modelValue="formData">
			<uni-forms-item>
			    <view class="label-container">
			      <label for="name" class="label-text">用户名</label>
				  <view class="item-text">
					  <uni-easyinput v-model="FormData.username" :value="FormData.username"></uni-easyinput>
				  </view>
			    </view>
			</uni-forms-item>
			<uni-forms-item>
			    <view class="label-container">
			      <label for="sex" class="label-text">性别</label>
				  <view class="item-text">
					  <!-- <uni-number-box v-model="FormData.height" :value="FormData.height"></uni-number-box> -->
					  <uni-data-select
					        v-model="FormData.sex"
					        :localdata="range"
					      ></uni-data-select>
				  </view>
			    </view>
			</uni-forms-item>
			<uni-forms-item>
			    <view class="label-container">
			      <label for="name" class="label-text">身高(cm)</label>
				  <view class="item-text">
					  <uni-number-box v-model="FormData.height" :value="FormData.height"></uni-number-box>
				  </view>
			    </view>
			</uni-forms-item>
			<uni-forms-item>
			    <view class="label-container">
			      <label for="name" class="label-text">体重(kg)</label>
				  <view class="item-text">
				  	  <uni-number-box v-model="FormData.weight" :value="FormData.weight"></uni-number-box>
				  </view>
			    </view>
			</uni-forms-item>
		</uni-forms>
		<button @click="submitForm">确定</button>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				FormData: {
					sex: 'man',
				},
				range: [
                        { value: 'man', text: "男" },
                        { value: 'woman', text: "女" },
                       ],
			}
		},
		created() {
			this.load();
		},
		methods: {
			load(){
		        const userId = uni.getStorageSync('user').id;
				this.request({url: '/user/getById?userId=' + userId, method: 'GET',data: this.form}).then(res => {
					if(res.code === '200'){
						this.FormData = res.data;
						console.log(this.FormData);
						
					}else{
						
					}
				})
			},
			submitForm(){
				console.log(this.FormData);
				this.request({url: '/user/update', method: 'POST',data: this.FormData}).then(res => {
					if(res.code === '200'){
						console.log("成功");
					}else{
						
					}
				})
			}
		}
	}
</script>

<style>
.label-container {
  display: flex;
  align-items: center;
}
.label-text {
  flex: 1;
  margin-right: 10px; /* 可根据需要调整间距 */
}
.item-text {
  flex: 1;
}
</style>
