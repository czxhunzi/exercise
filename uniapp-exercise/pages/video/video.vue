<template>
	<view class="container">
		<video @timeupdate="videoTimeUpdateEvent($event)" :src="src" controls></video>
	</view>
</template>


<script>
export default {
    data() {
        return {
			src: '',
			time: '',
			currentTime: '',
			currnetMinute: '',
			result: '',
			energy: '',
			modeId: '',
		}
    },
	onUnload() {
		let user = uni.getStorageSync('user');
		this.result  = this.currentTime / this.time * this.energy;
		console.log(this.result);
		let minute = Math.floor(this.currentTime/60);
		// minute = minute + 1;
		this.request({url: '/consume/add?userId=' + user.id + '&modeId=' + this.modeId + '&time=' + minute, method: 'POST'}).then(res => {
			if(res.code === '200'){

			}else{
				
			}
		});
	    console.log("页面被关闭");
	},
	// onNavigationBarButtonTap() {
	// 	console.log("success")		
	// },
	onLoad(query) {
		this.src = query.video;
		this.energy = query.energy;
		this.modeId = query.modeId;
		console.log(this.energy);
		this.prepare();
	},
    methods: {
		prepare(){
			this.time = 1;
			this.currentTime = 0;
			console.log("你好");
		},
		
		videoTimeUpdateEvent(e) { // 播放进度改变
		    // console.log(e)
			this.time = e.detail.duration;
		    this.currentTime = e.detail.currentTime;
		    // console.log('视频播放时长', e.detail.currentTime,'视频总时长', e.detail.duration);
		 },
	},
}
</script>


<style>
.container {
  display: flex;
  justify-content: center;
  /* align-items: center; */
  height: 100vh;
}
</style>
