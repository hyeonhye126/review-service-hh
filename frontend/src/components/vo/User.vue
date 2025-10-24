<template>
    <div style="margin: 13px 0 -15px 0">
        <v-card-title  v-if="inList && userEntity.name && userEntity.userId" style="font-size: 15px;">
            UserName: {{userEntity.name}}, Id: {{ userEntity.userId }}
        </v-card-title>
        <v-card-title  v-if="!inList">
            Your Profile
            <v-col
                cols="12"
                sm="3"
            >
                <v-btn
                v-if="avatarMode"
                icon
                color="grey"
                @click="changeUserCard()"
                >
                    <v-icon>mdi-credit-card</v-icon>
                </v-btn>
                <v-btn
                v-if="!avatarMode"
                icon
                color="grey"
                @click="changeUserCard()"
                >
                    <v-icon>mdi-account-box</v-icon>
                </v-btn>
            </v-col>
        </v-card-title>

        <v-card-text v-if="userEntity">
            <div v-if="editMode">
                <v-text-field label="Id" v-model="userEntity.userId"/>
            </div>
            <div v-if="editMode" style="margin-top: 5px;">
                <v-text-field label="Password" v-model="userEntity.password"/>
            </div>
            <div v-if="editMode" style="margin-top: 5px;">
                <v-text-field label="Name" v-model="userEntity.name"/>
            </div>
            <div v-if="editMode" style="margin-top: 5px;">
                <v-text-field label="Email" v-model="userEntity.email"/>
            </div>
            <div v-if="editMode" style="margin-top: 5px;">
                <v-text-field label="Address" v-model="userEntity.address"/>
            </div>
            <div v-if="editMode" style="margin-top: 5px;">
                <v-text-field label="Phone" v-model="userEntity.phone"/>
            </div>
            <div v-if="!editMode" style="margin-top: 5px;">
                <v-card
                    v-if="!editMode && avatarMode"
                    class="mx-auto"
                    max-width="200"
                    height="200"
                    style="margin-bottom:10px;"
                >
                    <v-row
                        align="center"
                        class="fill-height"
                    >
                        <v-col class="py-0">
                            <v-avatar width="120" height="120" color="white" style="margin: 15px 0 -5px 40px;">
                                <v-img
                                    :src="userEntity.profileImg ? userEntity.profileImg:'https://userEntity-images.githubusercontent.com/92732781/174538537-631a0ee2-40bb-4589-a58b-67da0ef17e38.png'"
                                    class="mx-auto"
                                ></v-img>
                            </v-avatar>
                            <v-list-item
                                color="rgba(0, 0, 0, .4)"
                            >
                                <v-list-item-content>
                                    <v-list-item-title class="text-h6" align="center">
                                        
                                    </v-list-item-title>
                                </v-list-item-content>
                            </v-list-item>
                        </v-col>
                    </v-row>
                </v-card>
                <v-card
                    v-if="!inList"
                    class="mx-auto"
                    max-width="400"
                    style="margin-bottom:10px;"
                >
                    <v-row
                        align="end"
                        class="fill-height"
                    >
                        <v-col
                        align-self="start"
                        class="pa-0"
                        cols="12"
                        >
                        </v-col>
                        <v-col class="py-0">
                        <v-avatar color="white" style="margin: 15px 0 -5px 15px;">
                            <v-img
                                :src="userEntity.profileImg ? userEntity.profileImg:'https://userEntity-images.githubusercontent.com/92732781/174538537-631a0ee2-40bb-4589-a58b-67da0ef17e38.png'"
                                class="mx-auto"
                            ></v-img>
                        </v-avatar>
                        <v-list-item
                            color="rgba(0, 0, 0, .4)"
                        >
                            <v-list-item-content>
                                <v-list-item-title class="text-h6">
                                    
                                </v-list-item-title>
                                <v-list-item-subtitle style="font-weight:500;">
                                    Id :  <br>
                                    Password :  <br>
                                    Email :  <br>
                                    Address :  <br>
                                    Phone :  
                                </v-list-item-subtitle>
                            </v-list-item-content>
                        </v-list-item>
                        </v-col>
                    </v-row>
                </v-card>
            </div>
        </v-card-text>
    </div>
</template>

<script>
    export default {
        name:"User",
        props: {
            editMode: Boolean,
            modelValue : Object,
            label : String,
            inList: Boolean
        },
        data: () => ({
            avatarMode : false,
            userEntity:{}
        }),
        created(){
            this.userEntity = this.modelValue
            if(!this.userEntity) {
                this.userEntity = {
                    'userId': '',
                    'password': '',
                    'name': '',
                    'email': '',
                    'address': '',
                    'phone': '',
                };
            }
        },
        watch: {
            userEntity(newVal) {
                this.$emit('update:modelValue', newVal);
            },
        },
        methods: {
            changeUserCard() {
                this.avatarMode = !this.avatarMode;
            }
        }
    }
</script>

<style scoped>
</style>
