<script setup>
import {
  VerticalNavLink,
  VerticalNavSectionTitle,
} from '@layouts'
</script>

<template>
    <ul>
        <VerticalNavSectionTitle :item="{ heading: '사용자관리' }" />
        <VerticalNavLink
            :item="{
                title: '사용자',
                to: '/customers',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: '주문관리' }" />
        <VerticalNavLink
            :item="{
                title: '주문',
                to: '/orders',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: '상점관리' }" />
        <VerticalNavLink
            :item="{
                title: '상점',
                to: '/stores',
            }"
        />
        <VerticalNavSectionTitle :item="{ heading: '리뷰관리' }" />
        <VerticalNavLink
            :item="{
                title: '리뷰',
                to: '/reviews',
            }"
        />
    </ul>
</template>
