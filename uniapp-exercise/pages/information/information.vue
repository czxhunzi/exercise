<template>
	<view>
		<view style="width: 90%; margin: 0rpx auto;">  <!-- 200rpx  == 100px -->
			<!-- <view style=" margin-bottom: 200rpx; font-size: 60rpx; color: royalblue; text-align: center;">你的个人信息</view> -->
			<uni-forms ref="form" :modelValue="form" :rules="rules">
				<uni-forms-item name="age">
					<text>请输入年龄吗？</text>
					 <uni-number-box v-model="form.age" min="10" max="100"></uni-number-box>
				</uni-forms-item>
				<uni-forms-item name="sex">
					<text>请输入性别吗？</text>
					 <uni-data-checkbox v-model="form.sex" :localdata="range"></uni-data-checkbox>
				</uni-forms-item>
				<uni-forms-item name="height">
					<text>请输入身高吗？（cm）</text>
					 <slider value="form.height" @change="sliderChange1" min="100" max="200" show-value step="1"/>
				</uni-forms-item>
				<uni-forms-item name="weight">
					<text>请输入体重吗？(kg)</text>
					 <slider value="form.height" @change="sliderChange2" min="40" max="100" show-value step="1"/>
				</uni-forms-item>
			</uni-forms>
			<view style="width: 90%; margin: 0rpx auto;">
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
			</view>
			<view>
				<button type="primary" @click="confirm">确定</button>
				<button type="default" @click="skip">不感兴趣</button>
			</view>
			<!-- <navigator url="/pages/register/register" style="margin: 40rpx 0; color: deepskyblue;">前往注册</navigator> -->
		</view>
	</view>
</template>

<script>
	
	export default {
		data() {
			return {
				form: {
					id: '',
					age: '',
					sex: '',
					height: '',
					weight: '',
					purposeId: 0,
					partId: 0,
				},
				range: [{"value": "man","text": "男"},{"value": "woman","text": "女"}],
				range1: [],
				range2: [],
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
			sliderChange(){
			},
			confirm(){
				this.form.id = uni.getStorageSync('user').id;
				console.log(this.form);
				this.request({url: '/user/confirm', method: 'POST',data: this.form}).then(res => {
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
			sliderChange1(e) {
				this.form.height = e.detail.value;
				
				// 滑动框很不同，需要一个事件控制数据变化
			},
			sliderChange2(e) {
				this.form.weight = e.detail.value;
				
				// 滑动框很不同，需要一个事件控制数据变化
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
