<template>
		<uni-list>
			<uni-list-item v-for="item in sportsList" :key="item.id" :title="item.name" :thumb="item.img" thumb-size="base" showArrow  clickable @click="handleItemClick(item)"></uni-list-item>
		</uni-list>				 
</template>

<script>
	export default {
		data() {
			return {
				sportsList: '',
			}
		},
		created() {
			this.load();
		},
		methods: {
			load(){
				this.request({url: '/mode/list', method: 'GET'}).then(res => {
					if(res.code === '200'){
						console.log(res.data);
						this.sportsList = res.data.map(data => {
							let newData = {id: data.id, name: data.name, }
							if(data.img){
								newData.img = uni.baseUrl + 'files/' + data.img;
							}
							return newData;
						})
						console.log(this.sportsList);
					}else{
						
					}
				});
			},
			handleItemClick(item) {
			  uni.navigateTo({
			  	url: '/pages/list/list?id=' + item.id
			  })
			},
		}
	}
</script>

<style>

</style>
