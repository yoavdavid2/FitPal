import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitpal.R
import com.example.fitpal.databinding.ItemPostBinding
import com.example.fitpal.model.Post

class PostAdapter(private val posts: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.authorImage.setImageResource(R.drawable.ic_profile_filled)
            binding.postTitle.text = post.title
            binding.postText.text = post.text
            binding.postImage.setImageResource(post.image)
            binding.likeCount.text = post.likes.toString()
            binding.commentCount.text = post.comments.toString()
            binding.postDate.text = post.date // Display the post date


            binding.btnLike.setOnClickListener {
                post.likes++
                binding.likeCount.text = post.likes.toString()
            }

            binding.btnComment.setOnClickListener {
                // Handle comment button click
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size
}
