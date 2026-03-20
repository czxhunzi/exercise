<template>
	<view>
		<button type="primary" @click="timeClick()">开始运动计时</button>
		
					<uni-card v-for="image in imagesList" :key="image">
						<view style="display: flex;">
							<image slot='cover' mode="widthFix" style="width: 50%;" :src="image.img" @click="playVideo(image)"></image>
							<view>
								<view class="uni-body">{{image.name}}</view>
								<view class="uni-body">{{image.time}}分钟 {{image.energy}}千卡</view>
							</view>
							
							
						</view>
						
						<view slot="actions" class="card-actions">
							<view class="card-actions-item" @click="actionsClick('分享')">
								<uni-icons type="redo-filled" size="18" color="#999"></uni-icons>
								<text class="card-actions-item-text">分享</text>
							</view>
							<view class="card-actions-item" @click="actionsClick('点赞')">
								<uni-icons type="heart" size="18" color="#999"></uni-icons>
								<text class="card-actions-item-text">点赞</text>
							</view>
						</view>
					</uni-card>
		
	</view>
</template>

<script>
	export default {
		data() {
			return {
				imagesList: [],
				id: '',
			}
		},
		onLoad(query) {
			this.id = query.id;
			this.request({url: '/course/condition/' + this.id, method: 'GET'}).then(res => {
				if(res.code === '200'){
					this.imagesList = res.data.map(data => {
						let newData = {id: data.id, modeId: data.modeId, name: data.name, time: data.time, energy: data.energy}
						if(data.img){
							newData.img = uni.baseUrl + 'files/' + data.img;
						}
						if(data.video){
							newData.video = uni.baseUrl + 'files/' + data.video;
						}
						return newData;
					})
					console.log(this.imagesList);
				}else{
					
				}
			});
			// console.log(this.id);
		},
		onBackPress(options) {
				console.log('from:' + options.from)
			},
		created() {
			// this.load();
		},
		methods: {
			timeClick(){
				uni.navigateTo({
					url: '/pages/time/time?id=' + this.id
				})
			},
			playVideo(image) {
			     console.log(image.modeId);
				 uni.navigateTo({
				 	url: '/pages/video/video?video=' + image.video + '&energy=' + image.energy + '&modeId=' + image.modeId
				 })
			},
			
		},
	}
</script>

<style>
	.card-actions {
		display: flex;
		flex-direction: row;
		justify-content: space-around;
		align-items: center;
		height: 30px;
		border-top: 1px #eee solid;
	}
	.card-actions-item {
		display: flex;
		flex-direction: row;
		align-items: center;
	}
	.card-actions-item-text {
		font-size: 12px;
		color: #666;
		margin-left: 5px;
	}

</style>
