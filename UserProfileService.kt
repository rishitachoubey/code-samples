class UserProfileService(
    private val userRepository: UserRepository,
    private val cache: Cache<String, UserProfile>
) {

    suspend fun getUserProfile(userId: String): UserProfile {
        cache[userId]?.let { return it }

        val profile = coroutineScope {

            val user = async {
                userRepository.getUser(userId)
            }

            val orders = async {
                userRepository.getRecentOrders(userId)
            }

            val preferences = async {
                userRepository.getPreferences(userId)
            }

            UserProfile(
                user = user.await(),
                recentOrders = orders.await(),
                preferences = preferences.await()
            )
        }

        cache.put(userId, profile)
        return profile
    }
}
