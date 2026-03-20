<template>
	<view>
		<!-- <uni-icons type="loop" size="30" open-type="getUserInfo" @click="getStep"></uni-icons> -->

		 <view class="charts-box">
		     <qiun-data-charts 
		       type="ring"
		       :opts="opts"
		       :chartData="chartData"
		     />
		</view>
		<view class="do">
			<button type="primary" @click="condition()" size="mini">运动详情</button>
		</view>
		<!-- 单纯的row+col并不好看，结合这个css的效果更好 -->
		<view style="background-color: white; margin: 5px 0; border-radius: 10px; padding: 10px 0;  max-width: 500px; overflow: hidden;">
		  <uni-row>
		    <uni-col :span='3' v-for="(item, index) in sportsList" :key="item.name" :offset="1">
		  				<!-- :key是vue自身内部用来操作 -->
		      <view class="grid-item-box"  @click="handleItemClick(item)" v-if="index < 4">
		        <image :src="item.img" mode="widthFix" style="width: 30%;"></image>
		        <text>{{ item.name }}</text>
		      </view>
		    </uni-col>
			<uni-col :span='3' v-if="sportsList.length > 3" :offset="1">
				<view class="grid-item-box"  @click="moreItem(item)">
				  <image :src="img" mode="widthFix" style="width: 30%;"></image>
				   <text style="margin-top: 2px; font-size: 15px;">更多</text>
				 </view>
			</uni-col>
		  </uni-row>
		</view>
		<view style="margin: 5px 0; font-size: 12px;">
		  <uni-row :gutter='10'>
			  <!-- gutter是每个col之间的间隔  -->
		    <uni-col :span='12' v-for="item in courseList" :key="item.name">
				<!-- :key是vue自身内部用来操作 -->
		      <view style="margin-bottom: 5px; background-color: white; padding: 10px; border-radius: 10px;">
		        <image :src="item.img" mode="widthFix" style="width: 100%;" @click="playVideo(item)"></image>
		        <view class="lineEllipsis">{{ item.name }}</view>
		       <!-- <view style="color: red; margin-top: 5px; font-weight: bold; font-size: 14px;">￥{{ item.price }}</view>
		        <view style="text-align: right;"><uni-icons type="plus" size="25" style="color: #666;" @click="addCart(item)"></uni-icons></view> -->
		      </view>
		    </uni-col>
		  </uni-row>
		</view>

	</view>
</template>

<script>
	import  WXBizDataCrypt from '../../utils/WXBizDataCrypt.js'
	export default {
		data() {
			return {
				precent: '',
				img: "../../static/more.png",
				notices: [
					{content: "第一条数据"},
					{content: "第二条数据"},
					{content: "第三条数据"},
				],
				notice: '',
				sportsList: [],
				courseList: [],
				showVideo: false,
				  chartData: {},
				      //您可以通过修改 config-ucharts.js 文件中下标为 ['ring'] 的节点来配置全局默认参数，如都是默认参数，此处可以不传 opts 。实际应用过程中 opts 只需传入与全局默认参数中不一致的【某一个属性】即可实现同类型的图表显示不同的样式，达到页面简洁的需求。
				      opts: {
				        rotate: false,
				        rotateLock: false,
				        color: ["#ffffff","#55aaff","#FAC858","#EE6666","#73C0DE","#3CA272","#FC8452","#9A60B4","#ea7ccc"],
				        padding: [5,5,5,5],
				        dataLabel: false,
				        enableScroll: false,
				        legend: {
				          show: false,
				          position: "right",
				          lineHeight: 25
				        },
				        title: {
				          name: "运动量",
				          fontSize: 15,
				          color: "#666666"
				        },
				        subtitle: {
				          name: "",
				          fontSize: 25,
				          color: "#7cb5ec"
				        },
				        extra: {
				          ring: {
				            ringWidth: 0,//将圆环变为饼状
				            activeOpacity: 0.5,
				            activeRadius: 10,
				            offsetAngle: -90,
							closeWise: false,
				            labelWidth: 15,
				            border: false,
				            borderWidth: 3,
				            borderColor: "#FFFFFF"
				          }
				        }
				      }
			}
		},
		onReady() {
		  this.getServerData();
		},
		onBackPress(options) {
				console.log('from:' + options.from)
			},
		onLoad() {
			// this.loadNotices();
			this.load();//返回后刷新页面
		},
		// a页面刚进入时，会触发a页面的onShow。
		
		// 当a跳转到b页面时，a会触发onHide，而b会触发onShow。
		
		// 但当b被关闭时，b会触发onUnload，此时a再次显示出现，会再次触发onShow。
		
		// 在tabbar页面（指pages.json里配置的tabbar），不同tab页面互相切换时，会触发各自的onShow和onHide
		onShow() {
			this.getServerData();
		},
		created(){
			this.load();
		},
		methods: {
			 condition() {
			      uni.navigateTo({
			      	url: '/pages/index/lead'
			      })
			},
			clickItem(item){
				console.log(item);
			},
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
					}else{
						
					}
				});
				this.request({url: '/course/list', method: 'GET'}).then(res => {
					if(res.code === '200'){
						console.log(res.data);
						this.courseList = res.data.map(data => {
							let newData = {id: data.id, modeId: data.modeId, name: data.name, time: data.time, energy: data.energy, popular: data.popular}
							if(data.img){
								newData.img = uni.baseUrl + 'files/' + data.img;
							}
							if(data.video){
								newData.video = uni.baseUrl + 'files/' + data.video;
							}
							return newData;
						})
					}else{
						
					}
				});
				
			},
			handleItemClick(item) {
			   uni.navigateTo({
			   	url: '/pages/list/list?id=' + item.id
			   })
			},
			moreItem(){
				uni.navigateTo({
					url: '/pages/list/more'
				})
			},
			 playVideo(item) {
			     console.log(item.video);
				 uni.navigateTo({
				 	url: '/pages/video/video?video=' + item.video + '&energy=' + item.energy + '&modeId=' + item.modeId
				 })
			},
			getServerData() {
			  //模拟从服务器获取数据时的延时
			  setTimeout(() => {
				  let user = uni.getStorageSync('user');
				  this.request({url: '/consume/getEnergy?userId=' + user.id, method: 'GET'}).then(res => {
				  	if(res.code === '200'){
						let data = res.data;
						this.precent = data.present / data.total * 100;
						console.log(this.precent);
						let result = [];
						for(let key in data){
							const obj = {"name": key, "value": data[key]};
							result.push(obj);
						}
						result[0].name = '还需消耗';
						result[1].name = '已消耗'
						console.log(result);
						let myres = {//命名不能冲突
						    series: [
						      {
						        data: result
						      }
						    ]
						  };
									    
						this.chartData = JSON.parse(JSON.stringify(myres));
				  	}else{
				  		
				  	}
				  });
			    
			  }, 500);
			},
		}
	}
</script>

<style>
  .grid-item-box {
	display: flex;
	flex-direction: column;
	/* 主轴为垂直方向，起点在上沿 */
	align-items: center;
	/* 交叉轴的中点对齐 */
	justify-content: center;
	/* 项目在主轴上居中对齐 */
	},
	.lineEllipsis {
			word-break: break-all;
			text-overflow: ellipsis;
			display: -webkit-box;
			-webkit-box-orient: vertical;
			-webkit-line-clamp: 2;
			/* 超出几行省略 */
			overflow: hidden;
	},
	.charts-box {
	    width: 100%;
	    height: 150px;
	  }
	.do{
		
		display: flex;
		justify-content: center;
		
	}  
	
</style>
