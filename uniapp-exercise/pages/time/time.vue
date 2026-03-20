<template>
	<view class="timer-container">
		 <text>{{sport}}</text>
		 <text class="timer">{{ formatTime(time) }}</text>
		 <text class="result">{{ calculateResult(time) }}</text>
		 <view class="btn-group">
			 <button  @click="toggleTimer">{{ timerPaused ? '继续' : '暂停' }}</button>
			 <button  @click="finish1">结束</button>
		 </view> 
	</view>
</template>

<script>
	export default {
	  data() {
	    return {
	      time: 0, // 初始时间为0
	      timer: null ,// 计时器对象
		  timerPaused: false ,// 计时器是否暂停
		  id: '',
		  energy: '',
		  result: '',
		  sport: '',
		  planId: '',
		  minutes: '',
	    };
	  },
	  onLoad(query) {
	  	this.id = query.id;
		this.planId = query.planId;
		console.log(this.planId);
		this.request({url: '/mode/getById/' + this.id, method: 'GET'}).then(res => {
			if(res.code === '200'){
				this.sport = res.data.name;
				this.energy = res.data.energy;
				console.log(this.energy);
			}else{
				
			}
		});
		console.log(this.id);
	  },
	  mounted() {
	    this.startTimer();
	  },
	  destroyed() {
	    this.stopTimer();
	  },
	  methods: {
	    startTimer() {
	      this.timer = setInterval(() => {
	        this.time++;
	      }, 1000);
	    },
	    stopTimer() {
	      clearInterval(this.timer);
	    },
	    formatTime(time) {
	      const hours = Math.floor(time / 3600);
	      this.minutes = Math.floor((time % 3600) / 60);
	      const seconds = time % 60;
	      return `${this.padZero(hours)}:${this.padZero(this.minutes)}:${this.padZero(seconds)}`;
	    },
	    padZero(num) {
	      return num.toString().padStart(2, '0');
	    },
		calculateResult(time) {
		      // const minutes = Math.floor(time / 60);
		      this.result = this.minutes * this.energy;
		      return `消耗能量: ${this.result}`;
		},
		toggleTimer() {
		      if (this.timerPaused) {
		        this.startTimer();
		      } else {
		        this.stopTimer();
		      }
		      this.timerPaused = !this.timerPaused;
		},
		finish(){
			this.stopTimer();
			console.log(this.result);
			let user = uni.getStorageSync('user');
			if(typeof this.planId === 'undefined'){
				this.request({url: '/energy/consume?id=' + user.id + '&egy=' + this.result, method: 'POST'}).then(res => {
					if(res.code === '200'){
						console.log("添加成功");
						uni.switchTab({
							url: '/pages/index/index'
						})
					}else{
						
					}
				});
			}else{
				this.request({url: '/energy/consume?id=' + user.id + '&egy=' + this.result, method: 'POST'}).then(res => {
					if(res.code === '200'){
						
					}else{		
					}
				});
				this.request({url: '/plan/count/' + this.planId + '/' + this.minutes, method: 'POST'}).then(res => {
					if(res.code === '200'){
						uni.switchTab({
							url: '/pages/plan/plan'
						})
					}else{
						
					}
				});
			}
		},
		finish1(){
			this.stopTimer();
			console.log(this.result);
			let user = uni.getStorageSync('user');
			this.request({url: '/consume/add?userId=' + user.id + '&modeId=' + this.id + '&time=' + this.minutes, method: 'POST'}).then(res => {
				if(res.code === '200'){
				}else{
					
				}
			});
		}
	  }
	};
</script>

<style>
.timer-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.timer {
  font-size: 36px;
  color: #333;
  font-weight: bold;
}

.result {
  font-size: 24px;
  color: #666;
  margin-top: 16px;
}
.btn-group {
  display: flex;
}
</style>
