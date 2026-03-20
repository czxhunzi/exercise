<template>
	<view>
		<uni-section>
			<uni-list>
				<uni-list-item v-for="item in foodList" :key="item.id" :title="item.name" :thumb="item.img" thumb-size="base" showArrow  clickable @click="handleItemClick(item)"></uni-list-item>
			</uni-list>
		</uni-section>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				categoryId: '',
				foodList: []
			}
		},
		onLoad(query) {
			this.categoryId = query.id;
			console.log(this.categoryId);
			this.request({url: '/food/getByCategoryId?categoryId=' + this.categoryId, method: 'GET'}).then(res => {
				if(res.code === '200'){
					console.log(res.data);
					this.foodList = res.data.map(data => {
						let newData = {id: data.id, name: data.name, }
						if(data.img){
							newData.img = uni.baseUrl + 'files/' + data.img;
						}
						return newData;
					})
				}else{
					
				}
			});
		},
		created() {
			
		},
		methods: {
			handleItemClick(item){
				uni.navigateTo({
					url: '/pages/food/detail?id=' + item.id
				})
			}
		}
	}
</script>

<style>

</style>
