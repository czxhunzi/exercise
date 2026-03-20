<template>
  <view class="charts-box">
    <qiun-data-charts 
      type="ring"
      :opts="opts"
      :chartData="chartData"
    />
	<uni-row class="demo-uni-row">
		<uni-col :span="8">
			<view class="demo-uni-col dark">碳水{{carbohydrate}}%</view>
		</uni-col>
		<uni-col :span="8">
			<view class="demo-uni-col light">蛋白质{{protein}}%</view>
		</uni-col>
		<uni-col :span="7" offset="1">
			<view class="demo-uni-col light">脂肪{{fat}}%</view>
		</uni-col>
	</uni-row>
	<view style="background-color: white; margin: 5px 0; border-radius: 10px; padding: 10px 0;  max-width: 500px; overflow: hidden;">
	  <uni-row>
	    <uni-col :span='6' v-for="(item, index) in categoryList" :key="item.name" >
	  				<!-- :key是vue自身内部用来操作 -->
	      <view class="grid-item-box"  @click="handleItemClick(item)" >
	        <image :src="item.img" mode="widthFix" style="width: 30%;"></image>
	        <text>{{ item.name }}</text>
	      </view>
	    </uni-col>
	  </uni-row>
	</view>
	<uni-section class="mb-10" title="食物推荐"></uni-section>
	<view style="background-color: white; margin: 0 0; border-radius: 10px; padding: 10px 0;  max-width: 500px; overflow: hidden;">
	  <uni-row >
	    <uni-col :span='8' v-for="(item, index) in foodList" :key="item.name" >
	  				<!-- :key是vue自身内部用来操作 -->
	      <view  class="grid-item-box" @click="recommend(item)" >
	        <image :src="item.img" mode="widthFix" style="width: 30%;"></image>
	        <text>{{ item.name }}</text>
	      </view>
	    </uni-col>
	  </uni-row>
	</view>
  </view>
</template>

<script>
export default {
  data() {
    return {
	  id: '',
	  precent: '',
	  carbohydrate: '',
	  protein: '',
	  fat: '',
	  categoryList:[],
	  foodList: [
		  {
			  img: "../../static/breakfast.png",
			  name: '早餐',
			  time: 1,
		  },
		  {
			  img: "../../static/lunch.png",
			  name: '中餐',
			  time: 2,
		  },
		  {
			  img: "../../static/dinner.png",
			  name: '晚餐',
			  time: 3,
		  }
	  ],
      chartData: {},
      //您可以通过修改 config-ucharts.js 文件中下标为 ['ring'] 的节点来配置全局默认参数，如都是默认参数，此处可以不传 opts 。实际应用过程中 opts 只需传入与全局默认参数中不一致的【某一个属性】即可实现同类型的图表显示不同的样式，达到页面简洁的需求。
      opts: {
        rotate: false,
        rotateLock: false,
        color: ["#1890FF","#ffffff","#FAC858","#EE6666","#73C0DE","#3CA272","#FC8452","#9A60B4","#ea7ccc"],
        padding: [5,5,5,5],
        dataLabel: false,
        enableScroll: false,
        legend: {
          show: false,
          position: "right",
          lineHeight: 25
        },
        title: {
          name: "已摄入",
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
            offsetAngle: -90, // 这里设置为 -90度，使第一个元素从圆环的中间显示
			closeWise: false,
            labelWidth: 15,
            border: false,
            borderWidth: 3,
            borderColor: "#FFFFFF"
          }
        }
      }
    };
  },
  onReady() {
    this.getServerData();
  },
  onShow() {
  	this.load();
	
	this.getServerData();
  },
  created() {
  	
	this.id = uni.getStorageSync('user').id;
  },
  methods: {
    getServerData() {
      //模拟从服务器获取数据时的延时
      setTimeout(() => {
        //模拟服务器返回数据，如果数据格式和标准格式不同，需自行按下面的格式拼接
		this.request({url: '/eat/getEnergy?id=' + this.id, method: 'GET'}).then(res => {
			if(res.code === '200'){
				console.log(res.data);
				if(res.data.energy===0){
					res.data.energy=1;
				}
				this.precent = Math.floor(res.data.energy/ (res.data.energy + res.data.standard) * 100);
				console.log(this.precent);
				this.opts.subtitle.name = this.precent + '%';
				this.carbohydrate = parseInt(res.data.carbohydrate / res.data.energy * 100);
				this.protein = parseInt(res.data.protein / res.data.energy * 100);
				this.fat = parseInt(res.data.fat / res.data.energy * 100);
				const {carbohydrate, protein, fat, ...rest} = res.data;
				const myRes = {
					series:[
						{
							data: Object.entries(rest).map(([key, value]) => ({name: key, value: value}))
						}
					]
				}
				console.log(myRes);
				this.chartData = JSON.parse(JSON.stringify(myRes));
			}else{
				
			}
		});
      }, 500);
    },
	load(){
		this.request({url: '/category/list', method: 'GET'}).then(res => {
			if(res.code === '200'){
				console.log(res.data);
				this.categoryList = res.data.map(data => {
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
	handleItemClick(item){
		uni.navigateTo({
			url: '/pages/food/list?id=' + item.id
		})
	},
	recommend(item){
		uni.navigateTo({
			url: '/pages/food/recommend?time=' + item.time
		})
	}
  }
};
</script>

<style scoped>
  /* 请根据实际需求修改父元素尺寸，组件自动识别宽高 */
  .charts-box {
    width: 100%;
    height: 150px;
  }
  .grid-item-box {
  	display: flex;
  	flex-direction: column;
  	/* 主轴为垂直方向，起点在上沿 */
  	align-items: center;
  	/* 交叉轴的中点对齐 */
  	justify-content: center;
  	/* 项目在主轴上居中对齐 */
  	}
</style>