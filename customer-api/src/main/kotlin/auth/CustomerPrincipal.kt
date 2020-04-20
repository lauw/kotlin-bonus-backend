package auth

import io.ktor.auth.Principal

data class CustomerPrincipal (
        val id: Int
): Principal