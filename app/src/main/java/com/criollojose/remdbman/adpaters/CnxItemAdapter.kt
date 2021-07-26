package com.criollojose.remdbman.adpaters

import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.criollojose.remdbman.R
import com.criollojose.remdbman.beans.ConectionItem
import com.criollojose.remdbman.databinding.CnxListItemBinding

class CnxItemAdapter:ListAdapter<ConectionItem,CnxItemAdapter.CnxViewHolder>(DiffCallback){
    companion object DiffCallback:DiffUtil.ItemCallback<ConectionItem>(){
        override fun areItemsTheSame(oldItem: ConectionItem, newItem: ConectionItem): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: ConectionItem, newItem: ConectionItem): Boolean {
            return oldItem==newItem
        }
    }

    lateinit var onItemNameListener:(ConectionItem)->Unit
    lateinit var onDeleteListener:(ConectionItem)->Unit
    lateinit var onUpdateListener:(ConectionItem)->Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CnxItemAdapter.CnxViewHolder {
        val binding=CnxListItemBinding.inflate(LayoutInflater.from(parent.context))
        return CnxViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CnxItemAdapter.CnxViewHolder, position: Int) {
        val cnxItem=getItem(position)
        holder.bind(cnxItem)
    }

    inner class CnxViewHolder(private val binding: CnxListItemBinding):RecyclerView.ViewHolder(binding.root){
        /*val cnxName=view.findViewById<TextView>(R.id.cnx_name)
        val cnxHost=view.findViewById<TextView>(R.id.cnx_host)
        val cnxUser=view.findViewById<TextView>(R.id.cnx_user)*/
        fun bind(cnxItem:ConectionItem){
            binding.cnxName.text=cnxItem.name
            binding.cnxHost.text=cnxItem.hostName
            binding.cnxUser.text=cnxItem.userName
            binding.deleteCnx.setOnClickListener{
                onDeleteListener(cnxItem)
            }
            binding.updateCnx.setOnClickListener {
                onUpdateListener(cnxItem)
            }
            binding.cnxName.setOnClickListener {
                onItemNameListener(cnxItem)
            }
        }
    }
}