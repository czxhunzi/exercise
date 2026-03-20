<template>
	<view>
		<uni-forms :modelValue="form">
			<uni-forms-item>
			    <view class="label-container">
			      <label for="name" class="label-text">食品名</label>
			      <view class="item-text">
			        {{ form.name }}
			      </view>
			    </view>
			</uni-forms-item>
			<uni-forms-item>
			<view class="label-container">
			      <label for="energy" class="label-text">所含能量(按100g计算)</label>
			      <view class="item-text">
			      </view>
			    </view>
			</uni-forms-item>
			<uni-forms-item>
			    <view class="label-container">
			      <label for="energy" class="label-text">热量</label>
			      <view class="item-text">
			        {{ form.energy }}
			      </view>
			    </view>
			</uni-forms-item>
			<uni-forms-item>
			    <view class="label-container">
			      <label for="carbohydrate" class="label-text">碳水化合物</label>
			      <view class="item-text">
			        {{ form.carbohydrate }}
			      </view>
			    </view>
			</uni-forms-item>
			<uni-forms-item>
			    <view class="label-container">
			      <label for="protein" class="label-text">蛋白质</label>
			      <view class="item-text">
			        {{ form.protein }}
			      </view>
			    </view>
			</uni-forms-item>
			<uni-forms-item>
			    <view class="label-container">
			      <label for="fat" class="label-text">脂肪</label>
			      <view class="item-text">
			        {{ form.fat }}
			      </view>
			    </view>
			</uni-forms-item>
			<uni-forms-item>
			   <uni-data-select
			     v-model="value"
			     :localdata="range"
				 placeholder="请选择重量"
			   ></uni-data-select>
			</uni-forms-item>
			 
			
		</uni-forms>
			<button @click="submitForm">添加进我的饮食</button>
		
	</view>
</template>

<script>
	export default {
		data() {
			return {
				id: '',
				userId: '',
				form: {},
				value: 1,
				range: [
				          { value: 0, text: "50g" },
				          { value: 1, text: "100g" },
				          { value: 2, text: "200g" },
				        ],
				
			}
		},
		onLoad(query) {
			this.id = query.id;
			this.userId = uni.getStorageSync('user').id;
			this.request({url: '/food/getById?id=' + this.id, method: 'GET'}).then(res => {
				if(res.code === '200'){
					console.log(res.data);
					this.form = res.data;
				}else{
					
				}
			});
		},
		methods: {
			submitForm(){
				this.request({url: '/eat/save?userId=' + this.userId + '&foodId=' + this.id + '&multiple=' + this.value, method: 'POST'}).then(res => {
					if(res.code === '200'){
						uni.showToast({
							title: '添加食物成功'
						})
					}else{
						
					}
				});
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
