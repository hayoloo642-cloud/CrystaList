{ lib, stdenv, appimageTools, fetchurl, glib-networking, release }:
let
  pname = "crystalist";
  version = release.version;
  asset = release.linux.${stdenv.hostPlatform.system};
  src = fetchurl { inherit (asset) url hash; };
  contents = appimageTools.extract { inherit pname version src; };
in
appimageTools.wrapType2 {
  inherit pname version src;
  # alsa-plugins would hide the host's /etc/alsa and its PipeWire configuration in the FHS sandbox.
  extraPkgs = pkgs: with pkgs; [
    gtk3 webkitgtk_4_1 libsecret dbus glib-networking
  ];
  profile = ''
    export GIO_EXTRA_MODULES="${glib-networking}/lib/gio/modules''${GIO_EXTRA_MODULES:+:$GIO_EXTRA_MODULES}"
  '';
  extraInstallCommands = ''
    install -Dm644 ${contents}/CrystaList.desktop $out/share/applications/CrystaList.desktop
    sed -i "s|^Exec=.*|Exec=$out/bin/crystalist %u|" $out/share/applications/CrystaList.desktop
    install -Dm644 ${contents}/CrystaList.png $out/share/icons/hicolor/512x512/apps/CrystaList.png
  '';
  meta = {
    description = "YouTube Music desktop client";
    homepage = "https://github.com/MetrolistGroup/CrystaList";
    license = lib.licenses.gpl3;
    platforms = [ "x86_64-linux" "aarch64-linux" ];
    mainProgram = pname;
  };
}
