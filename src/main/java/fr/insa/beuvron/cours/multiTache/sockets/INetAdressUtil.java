/*
Copyright 2000-2014 Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.beuvron.cours.multiTache.sockets;

import fr.insa.beuvron.utils.ConsoleFdB;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Quelques utilitaire liés à la gestion des adresses IP.
 *
 * @author francois
 */
public class INetAdressUtil {

    /**
     * Sur les systèmes Ubuntu (et peut-être d'autres, mais pas en windows), la
     * méthode {@code InetAddress.getLocalHost().getHostAddress()} retourne une
     * adresse loopback 127.0.1.1 ce qui ne permet pas d'y accéder sur une autre
     * machine. Cette méthode recherche l'ensemble des adresses et retourne la
     * liste des adresse qui . sont en IP V4 . qui ne sont pas des loopback .
     * qui ne sont pas multicast Cette méthode est reprise et adaptée de
     * {@link http://stackoverflow.com/questions/2381316/java-inetaddress-getlocalhost-returns-127-0-0-1-how-to-get-real-ip}
     *
     * @author francois
     */
    /**
     * Returns this host's non-loopback IPv4 addresses.
     *
     * @return
     * @throws SocketException
     */
    private static List<Inet4Address> getInet4Addresses() throws SocketException {
        List<Inet4Address> ret = new ArrayList<Inet4Address>();

        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        while (nets.hasMoreElements()) {
            NetworkInterface netint = nets.nextElement();
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (inetAddress instanceof Inet4Address
                        && !inetAddress.isLoopbackAddress()
                        && !inetAddress.isMulticastAddress()) {
                    ret.add((Inet4Address) inetAddress);
                }
            }
        }
        return ret;
    }

    /**
     *
     * @return première adresse IP V4 non loopback et non multicast sur la
     * machine locale
     * @throws SocketException si erreur ou aucune adresse convenable trouvée
     */
    public static Inet4Address premiereAdresseNonLoopback() throws SocketException {
        List<Inet4Address> alls = getInet4Addresses();
        if (alls.isEmpty()) {
            throw new SocketException("external IP adress not found");
        }
        return alls.get(0);
    }

    // -------------------------------------- des tests pour reverse DNS
    // --> pas très efficaces 
    // voir https://serverfault.com/questions/41064/whats-the-command-line-utility-in-windows-to-do-a-reverse-dns-look-up
    
    private static String reverseDNS(String ip) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(ip);
        System.out.println("host " + inetAddress.getHostName());
        System.out.println("host canonical " + inetAddress.getCanonicalHostName());
        InetAddress avecArpa = InetAddress.getByName(ip + ".in-addr.arpa");
        System.out.println("host arpa " + avecArpa.getHostName());
        System.out.println("host canonical arpa " + avecArpa.getCanonicalHostName());
        return inetAddress.getCanonicalHostName();
    }

    private static void testReverseDNS() {
        String ip = "";
        while (!ip.equalsIgnoreCase("FIN")) {
            ConsoleFdB.entreeString("entrez une adresse IP (FIN pour finir):");
            if (!ip.equalsIgnoreCase("FIN")) {
                try {
                    String host = reverseDNS(ip);
                    System.out.println("host trouvé : " + host);
                } catch (UnknownHostException ex) {
                    System.out.println("host inconnu");
                }
            }
        }
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        testReverseDNS();
    }

}
