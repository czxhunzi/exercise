<template>
	<view>
		<view class="container">
				<uni-section title="请输入基本信息" type="line">
					<view class="example">
						<!-- 基础用法，不包含校验规则 -->
						<uni-forms ref="baseForm" :modelValue="baseFormData">
							<uni-forms-item label="性别" required>
								 <uni-data-checkbox v-model="baseFormData.sex" :localdata="range"></uni-data-checkbox>
							</uni-forms-item>
							<uni-forms-item label="身高(cm)" required>
								 <slider v-model="baseFormData.height" @change="sliderChange1" min="150" max="220" show-value step="1"/>
							</uni-forms-item>
							<uni-forms-item label="体重(kg)" required>
								 <slider v-model="baseFormData.height" @change="sliderChange2" min="45" max="100" show-value step="1"/>
							</uni-forms-item>
							<uni-forms-item label="锻炼目的" required>
								<uni-data-checkbox v-model="baseFormData.purpose" :localdata="purposes" />
							</uni-forms-item>
							<uni-forms-item label="锻炼部位" required>
								<uni-data-checkbox v-model="baseFormData.part"  :localdata="parts" />
							</uni-forms-item>
							<uni-forms-item label="锻炼日" required>
								<uni-data-checkbox v-model="baseFormData.week" multiple :localdata="weeks" />
							</uni-forms-item>
							<!-- <uni-forms-item label="锻炼时间" required>
								<uni-data-checkbox v-model="baseFormData.time" multiple :localdata="times" />
							</uni-forms-item> -->
							<uni-forms-item label="喜爱运动" required>
								<uni-data-checkbox v-model="baseFormData.mode" multiple :localdata="displayModes" />
							</uni-forms-item>
							<button @click="showMoreOptions">换一批</button>
							
						</uni-forms>
					</view>
				</uni-section>
				<button type="primary" @click="confirm">确定</button>
				
				
		</view>

	</view>
</template>

<script>
	export default {
		data() {
			return {
				baseFormData: {
					                id: '',
					                sex: '',
					                height: '',
					                weight: '',
									purpose: 0,
									part: 0,
									week: [0],
									// time: [0],
									mode: [0],
								},
								// 单选数据源
								purposes:[],
								parts:[],
								modes:[],
								range: [{"value": "man","text": "男"},{"value": "woman","text": "女"}],
								// 多选数据源
								weeks: [{
									text: '星期1',
									value: 0
								}, {
									text: '星期2',
									value: 1
								}, {
									text: '星期3',
									value: 2
								}, {
									text: '星期4',
									value: 3
								}, {
									text: '星期5',
									value: 4
								}, {
									text: '星期6',
									value: 5
								}, {
									text: '星期天',
									value: 6
								}],
								times: [{
									text: '早锻炼',
									value: 0
								}, {
									text: '早晨和中午间',
									value: 1
								}, {
									text: '中午锻炼',
									value: 2
								}, {
									text: '中午和下午间',
									value: 3
								}, {
									text: '下午',
									value: 4
								}, {
									text: '下午和晚上间',
									value: 5
								}, {
									text: '晚上',
									value: 6
								}],
								showMore: false // 控制显示的选项数量
			}
		},
		onLoad(query) {
			const flag = query.flag;
			console.log(flag);
			if(flag==0){
				console.log("hhahh")
				const userId = uni.getStorageSync('user').id
				this.request({url: '/plan/deletePlan?userId=' + userId, method: 'POST'}).then(res => {
					if(res.code === '200'){
						console.log(res.data);
					}else{
						
					}
				});
			}
		},
		created() {
			this.load();
		},
		computed: {
		    displayModes() {
		      if (this.showMore) {
		        return this.modes.slice(3, 6); // 显示后3个选项
		      } else {
		        return this.modes.slice(0, 3); // 显示前3个选项
		      }
		    }
		},
		methods: {
			load(){
				this.request({url: '/purpose/list', method: 'GET'}).then(res => {
					if(res.code === '200'){
						console.log(res.data);
						this.purposes = res.data.map(item => {
						  return {
						    value: item.id,
						    text: item.name
						  };
						});
						console.log(this.purposes);
					}else{
						
					}
				});
				this.request({url: '/part/list', method: 'GET'}).then(res => {
					if(res.code === '200'){
						console.log(res.data);
						this.parts = res.data.map(item => {
						  return {
						    value: item.id,
						    text: item.name
						  };
						});
						console.log(this.parts);
					}else{
						
					}
				});
				this.request({url: '/mode/highList', method: 'GET'}).then(res => {
					if(res.code === '200'){
						console.log(res.data);
						this.modes = res.data.map(item => {
						  return {
						    value: item.id,
						    text: item.name
						  };
						});
						console.log(this.modes);
					}else{
						
					}
				});
				
			},
			confirm() {
				this.baseFormData.id = uni.getStorageSync('user').id;
				this.request({url: '/plan/newPlan', method: 'POST', data: this.baseFormData}).then(res => {
					if(res.code === '200'){
						console.log(res.data);
						uni.switchTab({
							url: '/pages/plan/plan'
						})
					}else{
						
					}
				});
				console.log(this.baseFormData);
			},
			showMoreOptions() {
			      this.showMore = !this.showMore; // 切换显示状态
			},
			sliderChange1(e) {
				this.baseFormData.height = e.detail.value;
				
				// 滑动框很不同，需要一个事件控制数据变化
			},
			sliderChange2(e) {
				this.baseFormData.weight = e.detail.value;
				
				// 滑动框很不同，需要一个事件控制数据变化
			},
		}
	}
</script>

<style>

</style>
