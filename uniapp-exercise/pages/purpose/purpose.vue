<template>
	<view>
		<view style="width: 90%; margin: 200rpx auto;">
			<uni-forms ref="form" :modelValue="form" :rules="rules">
				<uni-forms-item>
					<uni-data-select
					      v-model="form.purposeId"
					      :localdata="range1"
					    ></uni-data-select>
				</uni-forms-item>
				<uni-forms-item>
					<uni-data-select
					      v-model="form.partId"
					      :localdata="range2"
					    ></uni-data-select>
				</uni-forms-item>
			</uni-forms>
			<view>
				<button type="success" @click="confirm">完成</button>
			</view>
			<view>
				<button type="success" @click="skip()">跳过</button>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				range1: [],
				range2: [],
				form: {
					id: '',
					purposeId: 0,
					partId: 0,
				}
			}
		},
		created() {
			this.load();
		},
		methods: {
			load(){
				this.request({url: '/purpose/list', method: 'GET'}).then(res => {
					if(res.code === '200'){
						console.log(res.data);
						this.range1 = res.data.map(function(item) {
						  return { value: item.id, text: item.name };
						});
						console.log(this.range1);
					}else{
						
					}
				});
				this.request({url: '/part/list', method: 'GET'}).then(res => {
					if(res.code === '200'){
						console.log(res.data);
						this.range2 = res.data.map(function(item) {
						  return { value: item.id, text: item.name };
						});
						console.log(this.range2);
					}else{
						
					}
				});
			},
			confirm(){
				this.form.id = uni.getStorageSync('user').id;
				console.log(this.form);
				this.request({url: '/user/purpose', method: 'POST',data: this.form}).then(res => {
					if(res.code === '200'){
						uni.showToast({
							title: '信息录入成功',	
						});
						uni.switchTab({
							url: '/pages/index/index'
						})
					}else{
						uni.showToast({
							title: '信息录入失败'
						})
					}
				})
			},
			skip(){
				uni.switchTab({
					url: '/pages/index/index'
				})
			}
		}
	}
</script>

<style>

</style>
