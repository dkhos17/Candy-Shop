package com.example.clientapp.fragments

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clientapp.R
import com.example.clientapp.baseUrl
import com.example.clientapp.models.Message
import com.example.clientapp.models.Person
import com.example.clientapp.models.User
import com.example.clientapp.network.Client
import com.example.clientapp.recycler.ChatRecyclerViewAdapter
import com.example.clientapp.recycler.MessageRecyclerViewAdapter
import com.google.android.material.appbar.AppBarLayout
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs
import kotlin.math.roundToInt


class Chat:Fragment() {

    private lateinit var ivUserAvatar: CircleImageView
    private var EXPAND_AVATAR_SIZE: Float = 0F
    private var COLLAPSE_IMAGE_SIZE: Float = 0F
    private var horizontalToolbarAvatarMargin: Float = 0F
    private lateinit var toolbar: Toolbar
    private lateinit var appBarLayout: AppBarLayout
    private var cashCollapseState: Pair<Int, Int>? = null
    private lateinit var titleToolbarText: AppCompatTextView
    private lateinit var titleToolbarText2: AppCompatTextView
    private lateinit var titleToolbarTextSingle: AppCompatTextView
    private lateinit var titleToolbarTextSingle2: AppCompatTextView
    private lateinit var invisibleTextViewWorkAround: AppCompatTextView
    private lateinit var backButton: AppCompatButton
    private lateinit var sendButton: Button
    private lateinit var mess: EditText
    private lateinit var background: FrameLayout
    /**/
    private var avatarAnimateStartPointY: Float = 0F
    private var avatarCollapseAnimationChangeWeight: Float = 0F
    private var isCalculated = false
    private var verticalToolbarAvatarMargin =0F
    private lateinit var person : Person
    private lateinit var user: User
    private lateinit var whatido: AppCompatTextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            try {
                person = requireArguments().getSerializable("person") as Person
                user = requireArguments().getSerializable("me") as User
            } catch (e: Exception){}
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.chat, container, false)
        /**/
        EXPAND_AVATAR_SIZE = resources.getDimension(R.dimen.default_expanded_image_size)
        COLLAPSE_IMAGE_SIZE = resources.getDimension(R.dimen.default_collapsed_image_size)
        horizontalToolbarAvatarMargin = resources.getDimension(R.dimen.activity_margin)
        /* collapsingAvatarContainer = findViewById(R.id.stuff_container)*/
        appBarLayout = view.findViewById(R.id.app_bar_layout)
        toolbar = view.findViewById(R.id.anim_toolbar)
        ivUserAvatar = view.findViewById(R.id.chat_avatar)
        titleToolbarText = view.findViewById(R.id.chat_detail)
        titleToolbarText2 = view.findViewById(R.id.chat_detail2)
        titleToolbarTextSingle = view.findViewById(R.id.collapsed_chat_detail)
        titleToolbarTextSingle2 = view.findViewById(R.id.collapsed_chat_detail2)
        background = view.findViewById(R.id.fl_background)
        invisibleTextViewWorkAround = view.findViewById(R.id.chat_invisible)
        backButton = view.findViewById(R.id.back)
        sendButton = view.findViewById(R.id.sendButton)
        mess = view.findViewById(R.id.messageText)
        titleToolbarText.text = person.user.nick
        titleToolbarText2.text = person.user.todo

        titleToolbarTextSingle.text = person.user.nick
        titleToolbarTextSingle2.text = person.user.todo

        ivUserAvatar.setImageBitmap(BitmapFactory.decodeByteArray(person.user.avatar, 0, person.user.avatar.size))

        (toolbar.height - COLLAPSE_IMAGE_SIZE) * 2
        /**/
        appBarLayout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, i ->
                if (isCalculated.not()) {
                    avatarAnimateStartPointY = abs((appBarLayout.height - (EXPAND_AVATAR_SIZE + horizontalToolbarAvatarMargin)) / appBarLayout.totalScrollRange)
                    avatarCollapseAnimationChangeWeight = 1 / (1 - avatarAnimateStartPointY)
                    verticalToolbarAvatarMargin = (toolbar.height - COLLAPSE_IMAGE_SIZE) * 2
                    isCalculated = true
                }
                /**/
                updateViews(abs(i / appBarLayout.totalScrollRange.toFloat()))
            })

        val recycler = view.findViewById<RecyclerView>(R.id.chatsRecyclerView)
        val layout = LinearLayoutManager(context)
        layout.reverseLayout = false
        layout.stackFromEnd = true
        recycler.layoutManager = layout

        recycler.adapter = ChatRecyclerViewAdapter(findNavController(), person)
        val clientRetrofit = Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create()).build()
        val clientService: Client = clientRetrofit.create<Client>(Client::class.java)

        backButton.setOnClickListener {
            findNavController().navigate(R.id.action_chat_to_messageFragment2, bundleOf("user" to user))
        }

        fixedRateTimer("timer", false, 0L, 3000) {
            clientService.getMessages(Pair(user, person.user)).enqueue(object : Callback<List<Message>> {
                override fun onResponse(call: Call<List<Message>>, response: retrofit2.Response<List<Message>>) {
                    if(response.isSuccessful) {
                        response.body()?.let {
                            person.messages = it as MutableList<Message>
                            recycler.adapter = ChatRecyclerViewAdapter(findNavController(), person)
                        }
                    }
                }
                override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                    Log.d("connectserver", t.message!!)
                }
            })
        }

        sendButton.setOnClickListener {
            val mesigi = Message(from = user.nick, to = person.user.nick, message = mess.text.toString(), time = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("hh:mm a")))
            clientService.sendMessage(mesigi).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: retrofit2.Response<Void>) {
                    if(response.isSuccessful) {
                        Log.d("igzavnebaaa","aeee")
                        (recycler.adapter as ChatRecyclerViewAdapter).sendMessage(mesigi)
                    }
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Log.d("connectserver", t.message!!)
                }
            })
            mess.text.clear()
        }
        return view
    }

    private fun updateViews(offset: Float) {
        /* apply levels changes*/
        when (offset) {
            in 0.15F..1F -> {
                titleToolbarText.apply {
                    if (visibility != View.VISIBLE) visibility = View.VISIBLE
                    alpha = (1 - offset) * 0.35F
                }
                titleToolbarText2.apply {
                    if (visibility != View.VISIBLE) visibility = View.VISIBLE
                    alpha = (1 - offset) * 0.35F
                }
            }

            in 0F..0.15F -> {
                titleToolbarText.alpha = (1f)
                titleToolbarText2.alpha = (1f)
                ivUserAvatar.alpha = 1f
            }
        }

        /** collapse - expand switch*/
        when {
            offset < SWITCH_BOUND -> Pair(TO_EXPANDED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
            else -> Pair(TO_COLLAPSED, cashCollapseState?.second ?: WAIT_FOR_SWITCH)
        }.apply {
            when {
                cashCollapseState != null && cashCollapseState != this -> {
                    when (first) {
                        TO_EXPANDED ->  {
                            /* set avatar on start position (center of parent frame layout)*/
                            ivUserAvatar.translationX = 0F
                            /**/
                            background.setBackgroundColor(R.drawable.gradient)
                            /* hide top titles on toolbar*/
                            titleToolbarTextSingle.visibility = View.INVISIBLE
                            titleToolbarTextSingle2.visibility = View.INVISIBLE
                        }
                        TO_COLLAPSED -> background.apply {
                            alpha = 0F
                            setBackgroundColor(R.drawable.gradient)
                            animate().setDuration(250).alpha(1.0F)

                            /* show titles on toolbar with animation*/
                            titleToolbarTextSingle.apply {
                                visibility = View.VISIBLE
                                alpha = 0F
                                animate().setDuration(500).alpha(1.0f)
                            }

                            titleToolbarTextSingle2.apply {
                                visibility = View.VISIBLE
                                alpha = 0F
                                animate().setDuration(500).alpha(1.0f)
                            }
                        }
                    }
                    cashCollapseState = Pair(first, SWITCHED)
                }
                else -> {
                    cashCollapseState = Pair(first, WAIT_FOR_SWITCH)
                }
            }

            /* Collapse avatar img*/
            ivUserAvatar.apply {
                when {
                    offset > avatarAnimateStartPointY -> {
                        val avatarCollapseAnimateOffset = (offset - avatarAnimateStartPointY) * avatarCollapseAnimationChangeWeight
                        val avatarSize = EXPAND_AVATAR_SIZE - (EXPAND_AVATAR_SIZE - COLLAPSE_IMAGE_SIZE) * avatarCollapseAnimateOffset
                        this.layoutParams.also {
                            it.height = avatarSize.roundToInt()
                            it.width = avatarSize.roundToInt()
                        }
                        invisibleTextViewWorkAround.setTextSize(TypedValue.COMPLEX_UNIT_PX, offset)

                        this.translationX = ((appBarLayout.width - horizontalToolbarAvatarMargin - avatarSize) / 2) * avatarCollapseAnimateOffset / 3
                        this.translationY = ((toolbar.height  - verticalToolbarAvatarMargin - avatarSize ) / 2) * avatarCollapseAnimateOffset
                    }
                    else -> this.layoutParams.also {
                        if (it.height != EXPAND_AVATAR_SIZE.toInt()) {
                            it.height = EXPAND_AVATAR_SIZE.toInt()
                            it.width = EXPAND_AVATAR_SIZE.toInt()
                            this.layoutParams = it
                        }
                        translationX = 0f
                    }
                }
            }
        }
    }

    companion object {
        const val SWITCH_BOUND = 0.8f
        const val TO_EXPANDED = 0
        const val TO_COLLAPSED = 1
        const val WAIT_FOR_SWITCH = 0
        const val SWITCHED = 1
    }
}