import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.fitpal.R
import com.example.fitpal.model.Post

class PostsAdapter(private val posts: List<Post>) : BaseAdapter()  {

    override fun getCount(): Int = posts?.size ?: 0

    override fun getItem(position: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getItemId(position: Int): Long  = 0


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        TODO("Not yet implemented")

        val inflator = LayoutInflater.from(parent?.context)
        val view = convertView ?: inflator.inflate(R.layout.post_list_row, parent, false).apply {
            findViewById<ImageButton>(R.id.btnLike).apply {
                setOnClickListener {
                    (tag as? Int)?.let { tag ->
                        TODO("Like post with id $tag")
//                        val post = posts?.get(tag)
//                        post?.isChecked = (it as? CheckBox)?.isChecked ?: false
                    }
                }
            }
        }

        val post = posts?.get(position)

        val authorImageView: ImageView? = view?.findViewById(R.id.authorImage)
        val postTitleTextView: TextView? = view?.findViewById(R.id.postTitle)
        val postDateTextView: TextView? = view?.findViewById(R.id.postDate)
        val postTextTextView: TextView? = view?.findViewById(R.id.postText)
        val postImageView: ImageView? = view?.findViewById(R.id.postImage)
        val likeButtonImageButton: ImageButton? = view?.findViewById(R.id.btnLike)
        val commentButtonImageButton: ImageButton? = view?.findViewById(R.id.btnComment)
        val likeCountTextView: TextView? = view?.findViewById(R.id.likeCount)
        val commentCountTextView: TextView? = view?.findViewById(R.id.commentCount)


         authorImageView
         postImageView
         postTitleTextView?.text = post?.title
         postDateTextView?.text = post?.uploadDate
         postTextTextView?.text = post?.text
         likeCountTextView?.text = post?.likes.toString()
         commentCountTextView?.text = post?.comments.toString()


        likeButtonImageButton?.apply {
            TODO("Set like button state, if liked check, else uncheck")
            //isChecked = post?.isChecked ?: false
            tag = position
        }

        commentButtonImageButton?.apply {
            tag = position
        }

        return view!!
    }
}
