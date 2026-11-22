{ lib, stdenvNoCC, fetchurl, _7zz, release }:
let
  asset = release.darwin.${stdenvNoCC.hostPlatform.system};
in
stdenvNoCC.mkDerivation {
  pname = "crystalist";
  inherit (release) version;
  src = fetchurl { inherit (asset) url hash; };
  # The DMG is HFS+. 7zz keeps the runtime's symlinks with -snld.
  nativeBuildInputs = [ _7zz ];
  unpackPhase = ''
    runHook preUnpack
    7zz x -snld -y -ounpacked "$src" > /dev/null
    # Extended attributes come out as file:attribute files, which the signature does not cover.
    find unpacked -name '*:com.apple.*' -delete
    runHook postUnpack
  '';
  dontConfigure = true;
  dontBuild = true;
  # Stripping or patching would invalidate the bundle's signature.
  dontFixup = true;
  installPhase = ''
    runHook preInstall
    mkdir -p $out/Applications $out/bin
    cp -R unpacked/CrystaList/CrystaList.app $out/Applications/
    # The jpackage launcher finds its runtime relative to its own path, so it is called rather than linked.
    cat > $out/bin/crystalist <<SH
    #!/bin/sh
    exec "$out/Applications/CrystaList.app/Contents/MacOS/CrystaList" "\$@"
    SH
    chmod +x $out/bin/crystalist
    runHook postInstall
  '';
  meta = {
    description = "YouTube Music desktop client";
    homepage = "https://github.com/MetrolistGroup/CrystaList";
    license = lib.licenses.gpl3;
    platforms = [ "aarch64-darwin" "x86_64-darwin" ];
    sourceProvenance = [ lib.sourceTypes.binaryNativeCode ];
    mainProgram = "crystalist";
  };
}
