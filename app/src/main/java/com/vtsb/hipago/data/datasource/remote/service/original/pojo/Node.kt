package com.vtsb.hipago.data.datasource.remote.service.original.pojo

data class Node(
    val keys: Array<ShortArray>,
    val datas: Array<Data>,
    val subnode_addresses: LongArray
)
