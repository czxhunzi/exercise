<template>
	<view class="container">
	    <view class="left-panel">
	      <view class="weekday" v-for="(day, index) in weekdays" :key="index" @click="getDay(index)">
	        <text :class="{ 'highlight': isToday(index) }">{{ day }}</text>
	      </view>
	    </view>
	    <view class="right-panel">
		  	<uni-row  v-for="(dayObj, index) in transformedArray" :key="index">
		  		   <template v-if="dayObj.objects.length > 0">
		  		         <uni-col :span="6" v-for="(obj, objIndex) in dayObj.objects" :key="objIndex"  >
		  		           <view class="col with-border" @click="handleClick(obj)">
		  		             <!-- css一定要放对位置，否则它会不起作用 -->
		  		             {{ obj.mode }}
		  		           </view>
		  		         </uni-col>
		  		       </template>
		  		       <template v-else>
		  		         <uni-col :span="8">
		  		           <view class="col">
		  		               无计划
		  		           </view>
		  		         </uni-col>
		  		       </template>
		  	</uni-row>
	    </view>
		
		 <view>
		 		<uni-popup ref="popup1" type="center" :mask-click="false">
					<view class="uni-list">
						<view class="uni-list-cell">
							<view class="uni-list-cell-left">
								
							</view>
							<view class="uni-list-cell-db">
								<picker mode="time" :value="time" start="09:01" end="21:01" @change="bindTimeChange">
									<view class="uni-input">提醒时间 {{time}}</view>
								</picker>
							</view>
						</view>
					</view>
					<button  type="primary" @click="timeClick()">运动计时</text></button>
					<button  type="primary" @click="playVideo()">运动视频</text></button>
					<button @click="close">关闭</button>
				</uni-popup>
		 	</view>
		<uni-fab ref="fab" :pattern="pattern"  :horizontal="horizontal" :vertical="vertical"
					:direction="direction" @fabClick="make()"/>
		<uni-popup ref="popup2" type="bottom">
			<uni-popup-dialog ref="inputClose"  mode="base" title="计划已制定,确认制定新计划吗？" @confirm="dialogInputConfirm"></uni-popup-dialog>
		</uni-popup>
		<uni-popup ref="popup3" type="center">
			<uni-list>
				<uni-list-item v-for="item in day" :key="item.id" :title="item.mode" :right-text="`${item.time}分钟`"></uni-list-item>
			</uni-list>
			<view>
				总能量消耗{{total}}
			</view>
		</uni-popup>
	</view>
	
</template>

<script>
	export default {
		data() {
			return {
				time:"",
				weekdays: ['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期天'],
				list: [],
				id: '',
				plan: [],
				transformedArray: [],
				popupVisible: false,
				formData: {
				    name: '',
				    age: '',
				},
				text: '',
				total: '',
				day: [],
				directionStr: '垂直',
				horizontal: 'right',
				vertical: 'bottom',
				direction: 'horizontal',
				pattern: {
					color: '#7A7E83',
					backgroundColor: '#fff',
					selectedColor: '#007AFF',
					buttonColor: '#007AFF',
					iconColor: '#fff'
				},				
			}
		},
		onShow() {
			this.transformedArray = [];
			this.load();
		},
		created() {
			// this.load();
			
		},
		methods: {
				isToday(index) {
				     const now = new Date();
				     let weekday = now.getDay();
					 // console.log(weekday);
					 if(weekday === 0){
						 weekday = 7;
					 }
				     return index === weekday - 1;
				},
				send(){
					uni.requestSubscribeMessage({
						tmplIds: ['lnCQzBXb0Uy3FKKxTvgKPdrt5caLN1KW13KUrq2_Caw'],
						// 有意义，它要同意模板id；
						success(res){
							console.log(res);
						}
					})
				},
			    load(){
				    this.id = uni.getStorageSync('user').id;
					this.request({url: '/plan/getById/' + this.id, method: 'GET'}).then(res => {
						if(res.code === '200'){
							this.plan = res.data
							console.log(res.data);
							const currentDate = new Date(); // 获取当前日期和时间
							const currentDayOfWeek = currentDate.getDay(); // 获取当前星期几（返回值为0-6，其中0代表周日）
							const dayOfWeekMap = [
							  'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'
							];
							// const dayOffset = (currentDayOfWeek + 6) % 7; // 计算起始偏移量
														
							
							for (let i = 0; i < 7; i++) {
							  // const dayIndex = (i + dayOffset) % 7; // 计算当前日期索引
							  this.transformedArray.push({
							    dayOfWeek: dayOfWeekMap[i],
							    objects: []
							  });
							}
							
							this.plan.forEach((obj) => {
								this.request({url: '/mode/getById/' + obj.modeId, method: 'GET'}).then(res1 => {
									if(res1.code === '200'){
										  let dayOfWeek = new Date(obj.createtime).getDay(); // 获取obj.createtime对应的星期几
										   
										   if(dayOfWeek === 0){
											   dayOfWeek = 7;
										   }
										  const group = this.transformedArray[dayOfWeek-1];
										  let mode1 = res1.data.name;
										  group.objects.push({id: obj.id, time: obj.time, modeId: obj.modeId, mode: mode1, createtime: obj.createtime, notice: obj.notice});
										
									}else{
										
									}
								});
								
							})
							console.log(this.transformedArray);
							
							
						}else{
							
						}
					});
				},
				getObjectKey(obj) {
				    return obj.modeId; // 使用对象的 modeId 属性作为键值，这个不推荐使用，后面点击事件报错
				},
				 handleClick(obj) {
				    // 在这里执行点击事件的操作，可以访问点击的对象（obj）的属性
				    // console.log('点击了uni-col', obj);
					this.text = obj;
					
					if (obj.notice === null) {
					  console.log("对象为null");
					  this.time = "无";
					} else {
					  console.log("对象不为null");
					  this.time = obj.notice;
					}
					console.log(this.text);
					this.popupVisible = true; // 打开弹窗
					this.$refs.popup1.open('center')
					
				},
				timeClick(){
					this.$refs.popup1.close();//关闭弹窗
					uni.navigateTo({
						url: '/pages/time/time?id=' + this.text.modeId + '&planId=' + this.text.id
					})
				},
				playVideo(){
					this.$refs.popup1.close();
					uni.navigateTo({
						url: '/pages/list/list?id=' + this.text.modeId 
					})
				},
				make(){
					
					if(this.plan.length===0){
						this.$refs.popup1.close();
						uni.navigateTo({
							url: '/pages/plan/make?flag=' + 1
						})
					}else{
						this.$refs.popup2.open('center')
					}
					
				},
				dialogInputConfirm(){
					uni.navigateTo({
						url: '/pages/plan/make?flag=' + 0
					})
				},
			    bindTimeChange: function(e) {
					
					console.log(this.text.createtime);
			        this.time = e.detail.value
			    	console.log(this.time);
			    },
				close(obj){
					console.log(obj);
					// const notice = this.text.createtime + " " + this.time + ":00";
					if (this.time === null || this.time === undefined || this.time === '无') {
					  // 值为空
					} else {
					  this.send();
					  this.request({url: '/plan/notice?id=' + this.text.id + "&date=" + this.time, method: 'POST'}).then(res => {
					  	if(res.code === '200'){
					  		
					  	}else{
					  		
					  	}
					  });
					}
					
					// console.log(notice);
					this.$refs.popup1.close();
					if (this.time === null || this.time === undefined) {
					  // 值为空
					} else {
					  // 值不为空
					  this.transformedArray = [];
					  this.load();
					}
					
				},
				getDay(index){
					console.log(index);
					this.$refs.popup3.open('center')
					this.request({url: '/consume/getDay?userId=' + this.id + '&index=' + index, method: 'GET'}).then(res1 => {
						if(res1.code === '200'){
							console.log(res1.data);
							this.day = res1.data;
							this.request({url: '/energy/getDay?userId=' + this.id + '&index=' + index, method: 'GET'}).then(res2 => {
								if(res2.code === '200'){
									console.log(res2.data);
									if(res2.data == null){
										this.total = 0;
									}else{
										this.total = res2.data.consume
									}
									
									console.log(this.total);
								}else{
									
								}
							});
						}else{
							
						}
					});
				}
		}
	}
</script>

<style>
.container {
  display: flex;
}

.left-panel {
  background-color: bisque;
  width: 20%;
 
}
.right-panel {
  width: 80%;
}
.weekday{
	line-height: 4;
}
.highlight{
	color: aquamarine;
}
.col{
	margin-top: 23px;
	margin-bottom: 19px;
	
}
.with-border {
  border: 1px solid #000000;
  border-radius: 5px;
}
</style>
