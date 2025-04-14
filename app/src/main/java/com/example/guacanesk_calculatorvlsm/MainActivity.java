package com.example.guacanesk_calculatorvlsm;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextInputEditText editTextIp, editTextMascara, editTextNumRedes;
    private Button btnGenerarCamposRedes, btnCalcular, btnNueva;
    private LinearLayout contenedorRedes;
    private TableLayout tablaResultados;
    private List<Red> redes = new ArrayList<>();
    private List<Informacion> informacionRedes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextIp = findViewById(R.id.editTextIp);
        editTextMascara = findViewById(R.id.editTextMascara);
        editTextNumRedes = findViewById(R.id.editTextNumRedes);
        btnGenerarCamposRedes = findViewById(R.id.btnGenerarCamposRedes);
        btnCalcular = findViewById(R.id.btnCalcular);
        btnNueva = findViewById(R.id.btnNueva);
        contenedorRedes = findViewById(R.id.contenedorRedes);
        tablaResultados = findViewById(R.id.tablaResultados);

        btnGenerarCamposRedes.setOnClickListener(v -> generarCamposRedes());
        btnCalcular.setOnClickListener(v -> calcular());
        btnNueva.setOnClickListener(v -> reiniciar());
    }

    private void generarCamposRedes() {
        contenedorRedes.removeAllViews();
        redes.clear();

        String numRedesStr = editTextNumRedes.getText().toString();
        if (numRedesStr.isEmpty()) {
            Toast.makeText(this, "Ingrese el número de redes", Toast.LENGTH_SHORT).show();
            return;
        }

        int numRedes;
        try {
            numRedes = Integer.parseInt(numRedesStr);
            if (numRedes <= 0) {
                Toast.makeText(this, "El número de redes debe ser mayor que 0", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingrese un número válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generar campos para cada red
        for (int i = 0; i < numRedes; i++) {
            final int redIndex = i;
            LinearLayout redContainer = new LinearLayout(this);
            redContainer.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 16, 0, 0);
            redContainer.setLayoutParams(params);

            // Contenedor de la red y su nombre
            LinearLayout leftContainer = new LinearLayout(this);
            leftContainer.setOrientation(LinearLayout.VERTICAL);
            leftContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvRedNum = new TextView(this);
            tvRedNum.setText("Red " + (i + 1));
            tvRedNum.setTextSize(18);
            leftContainer.addView(tvRedNum);

            TextInputLayout tilNombre = new TextInputLayout(this);
            tilNombre.setHint("Nombre de Red ");
            TextInputEditText etNombre = new TextInputEditText(this);
            etNombre.setId(View.generateViewId());
            tilNombre.addView(etNombre);
            leftContainer.addView(tilNombre);

            redContainer.addView(leftContainer);

            // Contenedor de los hosts
            LinearLayout rightContainer = new LinearLayout(this);
            rightContainer.setOrientation(LinearLayout.VERTICAL);
            rightContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView tvHosts = new TextView(this);
            tvHosts.setText("Número de Hosts");
            tvHosts.setTextSize(18);
            rightContainer.addView(tvHosts);

            TextInputLayout tilHosts = new TextInputLayout(this);
            tilHosts.setHint(" ");
            TextInputEditText etHosts = new TextInputEditText(this);
            etHosts.setId(View.generateViewId());
            etHosts.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
            tilHosts.addView(etHosts);
            rightContainer.addView(tilHosts);

            redContainer.addView(rightContainer);

            contenedorRedes.addView(redContainer);

            redes.add(new Red(etNombre.getId(), etHosts.getId()));
        }
    }

    private void calcular() {
        // Validar campos principales
        String ip = editTextIp.getText().toString();
        String mascaraStr = editTextMascara.getText().toString();

        if (ip.isEmpty() || mascaraStr.isEmpty()) {
            Toast.makeText(this, "Ingrese la IP y máscara", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validación IP
        if (!esIpValida(ip)) {
            Toast.makeText(this, "IP inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        int mascara;
        try {
            mascara = Integer.parseInt(mascaraStr);
            if (mascara < 1 || mascara > 32) {
                if (mascara > 32) {
                    Toast.makeText(this, "La máscara de red no debe ser mayor a 32", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Máscara debe ser entre 1 y 32", Toast.LENGTH_SHORT).show();
                }
                return; // Detener el proceso si la máscara es inválida
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Máscara inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        // Recopilar información de redes
        List<RedInfo> redesInfo = new ArrayList<>();
        for (Red red : redes) {
            TextInputEditText etNombre = findViewById(red.getNombreId());
            TextInputEditText etHosts = findViewById(red.getHostsId());

            String nombre = etNombre.getText().toString();
            String hostsStr = etHosts.getText().toString();

            if (nombre.isEmpty() || hostsStr.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos de redes", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int hostCount = Integer.parseInt(hostsStr);
                if (hostCount <= 0) {
                    Toast.makeText(this, "El número de hosts debe ser mayor que 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                redesInfo.add(new RedInfo(nombre, hostCount));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Número de hosts inválido", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Ordenar redes por número de hosts (mayor a menor)
        Collections.sort(redesInfo, (a, b) -> b.getHosts() - a.getHosts());

        // Realizar cálculo de subnetting
        List<DireccionIP> subnetingResult = subneting(ip, mascara, redesInfo);

        // Generar información para cada red
        generarInformacion(subnetingResult, redesInfo);

        // Mostrar resultados
        generarTabla();

        // Deshabilitar botón calcular
        btnCalcular.setEnabled(false);
    }

    private void reiniciar() {
        editTextIp.setText("");
        editTextMascara.setText("");
        editTextNumRedes.setText("");
        contenedorRedes.removeAllViews();
        tablaResultados.removeAllViews();
        tablaResultados.setVisibility(View.GONE);
        redes.clear();
        informacionRedes.clear();
        btnCalcular.setEnabled(true);
    }

    private boolean esIpValida(String ip) {
        String[] octetos = ip.split("\\.");
        if (octetos.length != 4) return false;
        for (String octeto : octetos) {
            try {
                int valor = Integer.parseInt(octeto);
                if (valor < 0 || valor > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    private List<DireccionIP> subneting(String ip, int mascara, List<RedInfo> redesInfo) {
        List<DireccionIP> subredesCalculadas = new ArrayList<>();
        DireccionIP direccionIP = new DireccionIP(ip, mascara);
        subredesCalculadas.add(direccionIP);
        List<DireccionIP> subredesRetornar = new ArrayList<>();

        for (RedInfo redInfo : redesInfo) {
            int numHosts = redInfo.getHosts() + 2; // +2 para broadcast y dirección de red
            int bitsNecesarios = (int) Math.ceil(Math.log(numHosts) / Math.log(2));

            DireccionIP ipSubred = subredesCalculadas.get(0);

            if (ipSubred.getMascara() == 32 - bitsNecesarios) {
                subredesRetornar.add(subredesCalculadas.remove(0));
            } else {
                int bitsParaSubredes = 32 - ipSubred.getMascara() - bitsNecesarios;
                int cantidadSubredes = (int) Math.pow(2, bitsParaSubredes);
                String prefijoRed = ipSubred.getIpBIN().substring(0, ipSubred.getMascara());
                String sufijoHost = ipSubred.getIpBIN().substring(32 - bitsNecesarios);

                for (int indiceSubred = 0; indiceSubred < cantidadSubredes; indiceSubred++) {
                    String subredBinaria = Integer.toBinaryString(indiceSubred);
                    // Añadir ceros a la izquierda
                    while (subredBinaria.length() < bitsParaSubredes) {
                        subredBinaria = "0" + subredBinaria;
                    }

                    DireccionIP ipGuardada = new DireccionIP(
                            ipSubred.getipDEC(prefijoRed + subredBinaria + sufijoHost),
                            32 - bitsNecesarios
                    );

                    if (indiceSubred == 0) {
                        subredesRetornar.add(ipGuardada);
                        subredesCalculadas.remove(0);
                    } else {
                        subredesCalculadas.add(indiceSubred - 1, ipGuardada);
                    }
                }
            }
        }
        return subredesRetornar;
    }

    private void generarInformacion(List<DireccionIP> subnetingResult, List<RedInfo> redesInfo) {
        informacionRedes.clear();

        for (int i = 0; i < subnetingResult.size(); i++) {
            DireccionIP ipt = subnetingResult.get(i);
            String nombreRed = i < redesInfo.size() ? redesInfo.get(i).getNombre() : "Red " + (i + 1);

            informacionRedes.add(new Informacion(
                    nombreRed,
                    ipt.getIP(),
                    ipt.getPrimeraIP(),
                    ipt.getUltimaIP(),
                    ipt.getIPBroadcast(),
                    ipt.getMascara()
            ));
        }
    }

    private void generarTabla() {
        tablaResultados.removeAllViews();

        // Crear encabezado de la tabla
        TableRow headerRow = new TableRow(this);

        String[] headers = {"Red", "Dirección IP", "Primera IP", "Última IP", "IP Broadcast", "Máscara"};
        for (String header : headers) {
            TextView tv = new TextView(this);
            tv.setText(header);
            tv.setPadding(8, 8, 8, 8);
            tv.setTextSize(16);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            headerRow.addView(tv);
        }

        tablaResultados.addView(headerRow);

        // Agregar filas de datos
        for (Informacion info : informacionRedes) {
            TableRow row = new TableRow(this);

            addColumnToRow(row, info.getNombreRed());
            addColumnToRow(row, info.getDireccion_IP());
            addColumnToRow(row, info.getPrimera_IP());
            addColumnToRow(row, info.getUltima_IP());
            addColumnToRow(row, info.getIP_Broadcast());
            addColumnToRow(row, String.valueOf(info.getMascara_Red()));

            tablaResultados.addView(row);
        }

        // Make both the title and scroll view visible
        findViewById(R.id.tvResultadosTitulo).setVisibility(View.VISIBLE);
        findViewById(R.id.scrollResultados).setVisibility(View.VISIBLE);
        tablaResultados.setVisibility(View.VISIBLE);
    }

    private void addColumnToRow(TableRow row, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(8, 8, 8, 8);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        row.addView(tv);
    }

    // Clase para modelar una red (usada para referenciar los IDs de los campos)
    private static class Red {
        private final int nombreId;
        private final int hostsId;

        public Red(int nombreId, int hostsId) {
            this.nombreId = nombreId;
            this.hostsId = hostsId;
        }

        public int getNombreId() {
            return nombreId;
        }

        public int getHostsId() {
            return hostsId;
        }
    }

    // Clase para almacenar la información de cada red
    private static class RedInfo {
        private final String nombre;
        private final int hosts;

        public RedInfo(String nombre, int hosts) {
            this.nombre = nombre;
            this.hosts = hosts;
        }

        public String getNombre() {
            return nombre;
        }

        public int getHosts() {
            return hosts;
        }
    }

    // Clase para almacenar la información de cada subred calculada
    private static class Informacion {
        private final String nombreRed;
        private final String Direccion_IP;
        private final String Primera_IP;
        private final String Ultima_IP;
        private final String IP_Broadcast;
        private final int Mascara_Red;

        public Informacion(String nombreRed, String direccionIp, String direccionPrimera,
                           String direccionUltima, String direccionBroadcast, int mascara) {
            this.nombreRed = nombreRed;
            this.Direccion_IP = direccionIp;
            this.Primera_IP = direccionPrimera;
            this.Ultima_IP = direccionUltima;
            this.IP_Broadcast = direccionBroadcast;
            this.Mascara_Red = mascara;
        }

        public String getNombreRed() {
            return nombreRed;
        }

        public String getDireccion_IP() {
            return Direccion_IP;
        }

        public String getPrimera_IP() {
            return Primera_IP;
        }

        public String getUltima_IP() {
            return Ultima_IP;
        }

        public String getIP_Broadcast() {
            return IP_Broadcast;
        }

        public int getMascara_Red() {
            return Mascara_Red;
        }
    }

    // Clase DireccionIP (implementación del código original)
    private static class DireccionIP {
        private String ip;
        private int mascara;

        public DireccionIP(String ip, int mascara) {
            this.ip = ip;
            this.mascara = mascara;
        }

        public String getIP() {
            return this.ip;
        }

        public void setIP(String ip) {
            this.ip = ip;
        }

        public int getMascara() {
            return this.mascara;
        }

        public void setMascara(int mascara) {
            this.mascara = mascara;
        }

        public String getIpBIN() {
            String textoIP = this.ip;
            String[] octetos = textoIP.split("\\.");

            if (octetos.length != 4) {
                return "Dirección IP inválida";
            }

            StringBuilder ipBinaria = new StringBuilder();

            try {
                for (int i = 0; i < 4; i++) {
                    int octeto = Integer.parseInt(octetos[i]);

                    if (octeto < 0 || octeto > 255) {
                        return "Dirección IP inválida";
                    }

                    String octetoBinario = Integer.toBinaryString(octeto);
                    while (octetoBinario.length() < 8) {
                        octetoBinario = "0" + octetoBinario;
                    }

                    ipBinaria.append(octetoBinario);
                }

                return ipBinaria.toString();
            } catch (NumberFormatException e) {
                return "Dirección IP inválida";
            }
        }

        public String getMascaraBIN() {
            int mascaraSubred = this.mascara;

            if (mascaraSubred < 1 || mascaraSubred > 32) {
                return "Longitud de máscara de subred inválida";
            }

            StringBuilder mascaraBinaria = new StringBuilder();
            for (int i = 0; i < 32; i++) {
                if (i < mascaraSubred) {
                    mascaraBinaria.append("1");
                } else {
                    mascaraBinaria.append("0");
                }
            }

            return mascaraBinaria.toString();
        }

        public String getipDEC(String cadenaBinaria) {
            if (cadenaBinaria.length() != 32) {
                return null;
            }

            StringBuilder direccionIP = new StringBuilder();

            for (int i = 0; i < 4; i++) {
                String octeto = cadenaBinaria.substring(i * 8, (i + 1) * 8);
                int valorDecimal = Integer.parseInt(octeto, 2);
                direccionIP.append(valorDecimal);

                if (i < 3) {
                    direccionIP.append(".");
                }
            }

            return direccionIP.toString();
        }

        public String getPrimeraIP() {
            String ipBinario = getIpBIN();
            String primeraIPBinaria = ipBinario.substring(0, getMascara()) +
                    "0".repeat(32 - getMascara());
            String primeraIPDecimal = getipDEC(primeraIPBinaria);

            String[] octetos = primeraIPDecimal.split("\\.");
            octetos[3] = String.valueOf(Integer.parseInt(octetos[3]) + 1);

            return octetos[0] + "." + octetos[1] + "." + octetos[2] + "." + octetos[3];
        }

        public String getUltimaIP() {
            String ipBinario = getIpBIN();
            String ultimaIPBinaria = ipBinario.substring(0, getMascara()) +
                    "1".repeat(32 - getMascara());
            String ultimaIPDecimal = getipDEC(ultimaIPBinaria);

            String[] octetos = ultimaIPDecimal.split("\\.");
            octetos[3] = String.valueOf(Integer.parseInt(octetos[3]) - 1);

            return octetos[0] + "." + octetos[1] + "." + octetos[2] + "." + octetos[3];
        }

        public String getIPBroadcast() {
            String ipBinario = getIpBIN();
            String broadcastBinario = ipBinario.substring(0, getMascara()) +
                    "1".repeat(32 - getMascara());

            return getipDEC(broadcastBinario);
        }
    }
}