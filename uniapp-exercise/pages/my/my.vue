<template>
	<view>
		<view class="container" @click="message()">
		     <image :src="imageUrl" mode="widthFix" style="width: 30%;"></image>
		</view>
		<view class="grade">
			运动等级：LV4
		</view>
		<view>
			<button type="primary" @click="condition()">运动情况</button>
		</view>
		<view>
			<button type="primary">个人指数</button>
		</view>
	</view>
	
</template>

<script>
	export default {
		data() {
			return {
				imageUrl: '../../static/user.png'
			}
		},
		created() {
			
		},
		methods: {
			condition(){
				uni.navigateTo({
					url: '/pages/echarts/condition'
				})
			},
			message(){
				uni.navigateTo({
					url: '/pages/my/message'
				})
			}
		}
	}
</script>

<style>
.container {
  display: flex;
  flex-direction: column;
  /* 元素垂直排列 */
  align-items: center;
  /* 子元素在交叉轴居中对齐 */
  text-align: center;
   /* 文本在容器内水平居中，这不是关键属性 */
}

.text {
  margin-top: 10px;
}
.grade{
	 text-align: center;
}
</style>
