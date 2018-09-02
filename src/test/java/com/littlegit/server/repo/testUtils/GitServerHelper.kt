package com.littlegit.server.repo.testUtils

import com.littlegit.server.model.GitServer
import com.littlegit.server.model.GitServerRegion
import java.net.InetAddress

object GitServerHelper {
    fun createGitServer(id: Int = 1,
                        ip: InetAddress = InetAddress.getByName("192.0.2.0"),
                        region: GitServerRegion = GitServerRegion.UK,
                        capacity: Int = 10): GitServer = GitServer(id, ip, region, capacity)
}